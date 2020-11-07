package org.piangles.core.services.remoting.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.piangles.core.services.Request;

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
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
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

	@Override
	protected Object processRequest(Request request) throws Throwable
	{
		return null;
	}
}