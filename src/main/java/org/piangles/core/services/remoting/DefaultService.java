package org.piangles.core.services.remoting;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.piangles.core.services.Request;
import org.piangles.core.services.Response;
import org.piangles.core.services.Service;

public class DefaultService implements Service
{
	private HashMap<String, Method> endPointMethodMap = null;
	private Object delegate = null;

	public DefaultService(Object delegate)
	{
		this.delegate = delegate;
		endPointMethodMap = new HashMap<String, Method>();

		Method[] methods = delegate.getClass().getDeclaredMethods();
		for (Method method : methods)
		{
			endPointMethodMap.put(method.getName() + method.getParameterCount(), method);
		}
	}

	@Override
	public Response process(Request request)
	{
		Response response = null;
		Method method = null;
		Object[] args = request.getParameters();
		String key = null;
		if (args != null)
		{
			key = request.getEndPoint() + args.length;
		}
		else
		{
			key = request.getEndPoint() + "0";
		}
		
		method = endPointMethodMap.get(key);

		Object returnValue = null;
		if (method != null)
		{
			try
			{
				//This is where we make the actual call to the underlying service
				returnValue = method.invoke(delegate, args);
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

		response = new Response(request.getServiceName(), request.getEndPoint(), returnValue);

		return response;
	}
}
