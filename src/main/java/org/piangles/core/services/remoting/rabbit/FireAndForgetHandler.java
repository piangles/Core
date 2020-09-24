package org.piangles.core.services.remoting.rabbit;

import java.lang.reflect.Method;

import org.piangles.core.services.Request;
import org.piangles.core.services.remoting.handlers.AbstractHandler;
import org.piangles.core.services.remoting.handlers.HandlerException;

public final class FireAndForgetHandler extends AbstractHandler
{
	private RMQHelper rmqHelper = null;
	
	public FireAndForgetHandler(String serviceName)
	{
		super(serviceName);
	}

	@Override
	protected void init() throws HandlerException
	{
		try
		{
			rmqHelper = new RMQHelper(getServiceName(), false, getProperties());
			
			rmqHelper.getChannel().exchangeDeclare(rmqHelper.getRMQProperties().getTopic(), "fanout");
		}
		catch (Exception e)
		{
			throw new HandlerException(e);
		}
	}

	@Override
	public Object processMethodCall(Method method, Object[] args) throws Throwable
	{
		Request request = createRequest(method, args);

		//Make this into a Future or a Separate Thread
		rmqHelper.getChannel().basicPublish(rmqHelper.getRMQProperties().getTopic(), "", null, rmqHelper.getEncoder().encode(request));
		return null;
	}
	
	@Override
	public void destroy()
	{
		rmqHelper.destroy();
	}
}
