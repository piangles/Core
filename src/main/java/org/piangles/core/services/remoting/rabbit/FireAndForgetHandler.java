package org.piangles.core.services.remoting.rabbit;

import org.piangles.core.services.Request;
import org.piangles.core.services.remoting.handlers.AbstractHandler;
import org.piangles.core.services.remoting.handlers.HandlerException;

import com.rabbitmq.client.Channel;

public final class FireAndForgetHandler extends AbstractHandler
{
	private RMQHelper rmqHelper = null;
	private Channel channel = null;
	
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
			
			channel = rmqHelper.getConnection().createChannel();
			channel.exchangeDeclare(rmqHelper.getRMQProperties().getTopic(), "fanout");
		}
		catch (Exception e)
		{
			throw new HandlerException(e);
		}
	}

	@Override
	public Object processRequest(Request request) throws Throwable
	{
		//TODO: Make this into a Future or a Separate Thread
		channel.basicPublish(rmqHelper.getRMQProperties().getTopic(), "", null, rmqHelper.getEncoder().encode(request));
		return null;
	}
	
	@Override
	public void destroy()
	{
		rmqHelper.destroy();
	}
}
