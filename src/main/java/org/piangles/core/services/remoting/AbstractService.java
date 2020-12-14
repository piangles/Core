package org.piangles.core.services.remoting;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.piangles.core.services.Request;
import org.piangles.core.services.Response;
import org.piangles.core.services.Service;
import org.piangles.core.services.ServiceMetadata;

import com.mongodb.connection.Stream;

public abstract class AbstractService implements Service
{
	private ServiceMetadata serviceMetadata = null;
	private HashMap<String, Method> endPointMethodMap = null;
	private Object serviceImpl = null;

	public AbstractService(Object serviceImpl)
	{
		this.serviceImpl = serviceImpl;
		serviceMetadata = new ServiceMetadata();
		endPointMethodMap = new HashMap<String, Method>();

		Method[] methods = serviceImpl.getClass().getDeclaredMethods();
		for (Method method : methods)
		{
			endPointMethodMap.put(createKey(method.getName(), method.getParameters()), method);
			
			ServiceMetadata.Metadata metadata = new ServiceMetadata().new Metadata();
			metadata.streamBased = (method.getReturnType().getCanonicalName().equals(Stream.class.getCanonicalName()));
			
			serviceMetadata.addMetadata(method.getName(), metadata);
		}
	}

	@Override
	public final Response process(Request request)
	{
		Object returnValue = null;
		Method method = null;
		Object[] args = request.getParameters();
		
		method = endPointMethodMap.get(createKey(request.getEndPoint(), args));
		
		if (method != null)
		{
			try
			{
				//This is where we make the actual call to the underlying service
				returnValue = process(method, args, request);
			}
			catch (Exception e)
			{
				returnValue = e;
			}
		}
		else
		{
			returnValue = new RuntimeException("Endpoint : " +  request.getEndPoint() + " for Service : " + request.getServiceName() + " not found.");
		}
		
		return new Response(request.getServiceName(), request.getEndPoint(), returnValue);
	}
	
	@Override
	public final ServiceMetadata getServiceMetadata()
	{
		return serviceMetadata;
	}
	
	protected final Object getServiceImpl()
	{
		return serviceImpl;
	}

	private String createKey(String endpoint, Object[] args)
	{
		String key = null;
		if (args != null)
		{
			key = endpoint + args.length;
		}
		else
		{
			key = endpoint + "0";
		}

		return key;
	}
	
	protected abstract Object process(Method method, Object[] args, Request request) throws Exception;
}
