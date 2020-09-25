package org.piangles.core.services.remoting;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.piangles.core.services.Context;
import org.piangles.core.services.Request;
import org.piangles.core.services.Response;
import org.piangles.core.services.Service;

public class AuditableControllerServiceDelegate implements Service
{
	private HashMap<String, Method> endPointMethodMap = null;
	private Object serviceImpl = null;

	public AuditableControllerServiceDelegate(Object serviceImpl)
	{
		this.serviceImpl = serviceImpl;
		endPointMethodMap = new HashMap<String, Method>();

		Method[] methods = serviceImpl.getClass().getDeclaredMethods();
		for (Method method : methods)
		{
			endPointMethodMap.put(method.getName(), method);
		}
	}

	@Override
	public Response process(Request request)
	{
		Response response = null;
		Object[] args = request.getParameters();
		Method method = endPointMethodMap.get(request.getEndPoint());

		Object returnValue = null;
		try
		{
			Context context = new Context(request.getTraceId(), request.getHeader(), request.getSourceInfo()); 
			Object[] modifiedArgs = new Object[1 + args.length];
			modifiedArgs[0] = context;
			System.arraycopy(args, 0, modifiedArgs, 1, args.length);
			
			returnValue = method.invoke(serviceImpl, modifiedArgs);
		}
		catch (Exception e)
		{
			returnValue = e;
		}

		response = new Response(request.getServiceName(), request.getEndPoint(), returnValue);

		return response;
	}
}
