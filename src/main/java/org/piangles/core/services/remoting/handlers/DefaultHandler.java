package org.piangles.core.services.remoting.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.piangles.core.services.Request;
import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamDetails;

public final class DefaultHandler extends AbstractHandler
{
	private Object service = null;

	public DefaultHandler(Object service)
	{
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

	@Override
	protected Stream createStream(StreamDetails details) throws Exception
	{
		return null;
	}
}