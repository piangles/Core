package com.TBD.core.services.remoting.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class DefaultHandler extends AbstractHandler
{
	private Object service = null;

	public DefaultHandler(Object service)
	{
		super(service.getClass().getName());
		this.service = service;
	}

	@Override
	protected void init()
	{
		
	}

	@Override
	protected Object processMethodCall(Method method, Object[] args)
	{
		Object result = null;
		try
		{
			result = method.invoke(service, args);
		}
		catch (IllegalAccessException e)
		{
			result = e;
		}
		catch (IllegalArgumentException e)
		{
			result = e;
		}
		catch (InvocationTargetException e)
		{
			result = e;
		}
		return result;
	}
}