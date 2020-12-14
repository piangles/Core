package org.piangles.core.services.remoting;

import java.lang.reflect.Method;

import org.piangles.core.services.Request;

public class DefaultService extends AbstractService
{
	public DefaultService(Object serviceImpl)
	{
		super(serviceImpl);
	}
	
	@Override
	protected Object process(Method method, Object[] args, Request request) throws Exception
	{
		return method.invoke(getServiceImpl(), args);
	}
}
