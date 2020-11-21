package org.piangles.core.services.remoting.rabbit;

import org.piangles.core.resources.RabbitMQSystem;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.services.Request;
import org.piangles.core.services.remoting.handlers.AbstractHandler;
import org.piangles.core.services.remoting.handlers.HandlerException;
import org.piangles.core.util.InMemoryConfigProvider;

import com.rabbitmq.client.Channel;

public final class FireAndForgetHandler extends AbstractHandler
{
	private RabbitMQSystem rmqSystem = null;
	private Channel channel = null;
	
	@Override
	protected void init() throws HandlerException
	{
		try
		{
			rmqSystem = ResourceManager.getInstance().getRabbitMQSystem(new InMemoryConfigProvider(getServiceName(), getProperties()));
			
			channel = rmqSystem.getConnection().createChannel();
			channel.exchangeDeclare(RabbitProps.getTopic(getProperties()), "fanout");
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
		channel.basicPublish(RabbitProps.getTopic(getProperties()), "", null, getEncoder().encode(request));
		return null;
	}
	
	@Override
	public void destroy()
	{
		rmqSystem.destroy();
	}
}
