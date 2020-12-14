package org.piangles.core.services.remoting.handlers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.piangles.core.services.Header;
import org.piangles.core.services.Request;
import org.piangles.core.services.remoting.AbstractRemoter;
import org.piangles.core.services.remoting.SessionAwareable;
import org.piangles.core.services.remoting.Traceable;
import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamDetails;
import org.piangles.core.util.SystemHelper;

public abstract class AbstractHandler extends AbstractRemoter implements Handler
{
	private static final String PROPS_REQ_CREATOR = "RequestCreatorClassName";
	
	private static Set<Class<?>> WRAPPER_TYPES = null;
	private Header header = null;
	private RequestCreator requestCreator = null; 
	
	@Override
	public final void init(String serviceName, Properties properties) throws HandlerException
	{
		try
		{
			super.init(serviceName, properties);
		}
		catch (Exception e)
		{
			throw new HandlerException(e);
		}
		
		WRAPPER_TYPES = new HashSet<Class<?>>();
        WRAPPER_TYPES.add(Boolean.class);
        WRAPPER_TYPES.add(Character.class);
        WRAPPER_TYPES.add(Byte.class);
        WRAPPER_TYPES.add(Short.class);
        WRAPPER_TYPES.add(String.class);
        WRAPPER_TYPES.add(Integer.class);
        WRAPPER_TYPES.add(Long.class);
        WRAPPER_TYPES.add(Float.class);
        WRAPPER_TYPES.add(Double.class);
        WRAPPER_TYPES.add(Void.class);
        
		String hostName = SystemHelper.getHostName();
		String loginId = SystemHelper.getLoginId();
		String processName = SystemHelper.getProcessName();
		String processId = SystemHelper.getProcessId();
		String threadId = SystemHelper.getThreadId();

		header = new Header(hostName, loginId, processName, processId, threadId);
		
		String requestCreatorClassName = properties.getProperty(PROPS_REQ_CREATOR);
		try
		{
			if (requestCreatorClassName != null)
			{
				requestCreator = (RequestCreator)Class.forName(requestCreatorClassName).newInstance();
			}
			else
			{
				requestCreator = new DefaultRequestCreator();
			}
		}
		catch (Exception e)
		{
			throw new HandlerException(e);
		}
		init();
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		long startTime = System.nanoTime();
		Object result = null;
		Request request = null; 
		try
		{
			request = createRequest(method, args);

			result = processRequest(request);
			
			if (result instanceof StreamDetails)
			{
				result = createStream((StreamDetails)result);
			}
		}
		catch(Throwable t)
		{
			String message = t.getMessage();
			if (message == null)
			{
				message = t.getClass().getCanonicalName();
			}
			result = createException(method, "Unable to process call " + endpoint(request) +  " because of: " + message, t);
		}
		finally
		{
			long delayNS = System.nanoTime() - startTime;
			long delayMiS = TimeUnit.NANOSECONDS.toMicros(delayNS);
			long delayMS = TimeUnit.NANOSECONDS.toMillis(delayNS);
			String traceId = null;
			if (request != null && request.getTraceId() != null)
			{
				traceId = request.getTraceId().toString();
			}
			System.out.println(String.format("CallerSide-TimeTaken for traceId %s by %s is %d MilliSeconds and %d MicroSeconds.", traceId, endpoint(request), delayMS, delayMiS));
		}
		
		/**
		 * Result will always be :
		 * 1. A Java Object
		 * 2. Exception
		 */
		
		if (result instanceof Throwable)
		{
			Throwable actualThrowable = null;
			if (result instanceof java.lang.reflect.InvocationTargetException)
			{
				actualThrowable = ((Throwable)result).getCause();
			}
			else
			{
				actualThrowable = (Throwable)result;
			}
			throw actualThrowable;
		}
		
		return result;
	}
	
	protected final Request createRequest(Method method, Object[] args) throws Throwable
	{
		return requestCreator.createRequest(getUserId(), getSessionId(), getOrCreateTraceId(), getServiceName(), header, method, args);
	}

	protected final Exception createException(Method method, String message, Throwable cause)
	{
		Exception transformedExpt = null;

		try
		{
			Class<?> exptClass = method.getExceptionTypes()[0];
			Constructor<?> constructor = exptClass.getConstructor(String.class, Throwable.class);
			transformedExpt = (Exception) constructor.newInstance(new Object[] { message, cause });
		}
		catch (Exception expt)
		{
			String transFailedMessage = null;
			if (expt instanceof ArrayIndexOutOfBoundsException)//This implies the signature never declared an exception
			{
				transFailedMessage = "Service method does not have exception declaration. Root Cause[" + message + "]";				
			}
			else
			{
				transFailedMessage =  "Unable to convert server side exception. Root Cause[" + message + "]";
			}

			transformedExpt = new RuntimeException(transFailedMessage, cause);
		}

		return transformedExpt;
	}

	protected final boolean isPrimitiveOrWrapper(Object value)
	{
		
		return isPrimitiveOrWrapper(value.getClass());
	}
	
	protected final boolean isPrimitiveOrWrapper(Class<?> valueClass)
	{
		boolean primitiveOrWrapper = false;

		primitiveOrWrapper = valueClass.isPrimitive();
		if (!primitiveOrWrapper)
		{
			primitiveOrWrapper = WRAPPER_TYPES.contains(valueClass);
		}
		
		return primitiveOrWrapper;
		
	}

	protected final Header getHeader()
	{
		return header;
	}
	
	protected final String endpoint(Request request)
	{
		String endpoint = null;
		if (request != null)
		{
			endpoint = request.getEndPoint();
		}
		return getServiceName() + "::" + endpoint;
	}
	
	private UUID getOrCreateTraceId()
	{
		UUID traceId = null;

		Object currentThread = Thread.currentThread();
		if (currentThread instanceof Traceable)
		{
			traceId = ((Traceable)currentThread).getTraceId();
		}
		else
		{
			traceId = UUID.randomUUID();
		}
		
		return traceId;
	}
	
	private String getSessionId()
	{
		String sessionId = null;
		
		Object currentThread = Thread.currentThread();
		if (currentThread instanceof SessionAwareable)
		{
			sessionId = ((SessionAwareable)currentThread).getSessionDetails().getSessionId();
		}
		
		return sessionId;
	}

	private String getUserId()
	{
		String userId = null;
		
		Object currentThread = Thread.currentThread();
		if (currentThread instanceof SessionAwareable)
		{
			userId = ((SessionAwareable)currentThread).getSessionDetails().getUserId();
		}
		
		return userId;
	}

	protected abstract void init() throws HandlerException;
	protected abstract Object processRequest(Request request) throws Throwable;
	protected abstract Stream createStream(StreamDetails details) throws Exception;
}


