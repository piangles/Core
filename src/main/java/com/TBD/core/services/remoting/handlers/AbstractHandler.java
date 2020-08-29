package com.TBD.core.services.remoting.handlers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import com.TBD.core.services.Header;
import com.TBD.core.services.Request;
import com.TBD.core.services.remoting.SessionAwareable;
import com.TBD.core.services.remoting.Traceable;
import com.TBD.core.util.SystemHelper;

public abstract class AbstractHandler implements Handler
{
	private static final String PROPS_REQ_CREATOR = "RequestCreatorClassName";
	
	private static Set<Class<?>> WRAPPER_TYPES = null;
	private String serviceName = null;
	private Properties properties = null;
	private Header header = null;
	private RequestCreator requestCreator = null; 
	
	public AbstractHandler(String serviceName)
	{
		this.serviceName = serviceName;
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
	}
	
	@Override
	public final void init(Properties properties) throws HandlerException
	{
		this.properties = properties;
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
	public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		long startTime = System.currentTimeMillis();
		Object result = null;
		
		try
		{
			result = processMethodCall(method, args);
		}
		catch(Throwable t)
		{
			result = createException(method, "Unable to process call because of:" + t.getClass().getSimpleName(), t);
		}
		finally
		{
			System.out.println(String.format("CallerSide-TimeTaken by %s is %d MilliSeconds.", method.getName(), (System.currentTimeMillis() - startTime)));
		}
		
		/**
		 * Result will always be :
		 * 1. A Java Object
		 * 2. Exception
		 */
		
		if (result instanceof Throwable)
		{
			throw (Throwable)result;
		}
		
		return result;
	}
	
	protected final Request createRequest(Method method, Object[] args) throws Throwable
	{
		return requestCreator.createRequest(getUserId(), getSessionId(), getOrCreateTraceId(), serviceName, header, method, args);
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
			if (expt instanceof ArrayIndexOutOfBoundsException)
			{
				transFailedMessage = "Service method does not have exception declaration.";				
			}
			else
			{
				transFailedMessage = getClass().getSimpleName() + " unable to convert server side Exception. Reason[" + expt.getClass().getSimpleName() + "]";
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

	protected final String getServiceName()
	{
		return serviceName;
	}
	
	protected final Header getHeader()
	{
		return header;
	}
	
	protected final Properties getProperties()
	{
		return properties;
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
			//str = str.replaceAll(".", "*");
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
	protected abstract Object processMethodCall(Method method, Object[] args) throws Throwable;
}


