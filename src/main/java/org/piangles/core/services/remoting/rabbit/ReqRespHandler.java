package org.piangles.core.services.remoting.rabbit;

import java.util.UUID;

import org.piangles.core.resources.RabbitMQSystem;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.services.Request;
import org.piangles.core.services.Response;
import org.piangles.core.services.remoting.handlers.AbstractHandler;
import org.piangles.core.services.remoting.handlers.HandlerException;
import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamDetails;
import org.piangles.core.util.InMemoryConfigProvider;
import org.piangles.core.util.coding.JAVA;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.RpcClient;
import com.rabbitmq.client.RpcClientParams;

public final class ReqRespHandler extends AbstractHandler
{
	private RabbitMQSystem rmqSystem = null;
	
	@Override
	protected void init() throws HandlerException
	{
		try
		{
			rmqSystem = ResourceManager.getInstance().getRabbitMQSystem(new InMemoryConfigProvider(getServiceName(), getProperties()));
		}
		catch (Exception e)
		{
			throw new HandlerException(e);
		}

	}

	@Override
	protected Object processRequest(Request request) throws Throwable
	{
		Object returnValue = null;

		String corrId = UUID.randomUUID().toString();
		Channel channel = rmqSystem.getConnection().createChannel();
		
		/**
		 * TODO exchange will have to derived from Topic eventually for load balancing.
		 *
		 * This is not neeeded any more, the replyTo is defaulted in BasicProperties
		 * String replyQueueName = channel.queueDeclare().getQueue();
		 * BasicProperties.replyTo(replyQueueName)
		 * queueDeclare creates a responseQueue which is nondurable and autodeleted
		 */
		String exchange = "";
		
		BasicProperties props = new BasicProperties.Builder()
									.correlationId(corrId)
									.build();

		RpcClientParams params = new RpcClientParams().
									channel(channel).
									exchange(exchange).
									routingKey(RabbitProps.getTopic(getProperties())).
									timeout((int)RabbitProps.getTimeout(getProperties()));
		
		RpcClient rpcClient = new RpcClient(params);
		byte[] responseAsBytes = rpcClient.doCall(props, getEncoder().encode(request)).getBody();
		rpcClient.close();
		channel.close();
		
		Response response = null;
		if (responseAsBytes != null)
		{
			response = JAVA.getDecoder().decode(responseAsBytes, Response.class);
			returnValue = response.getReturnValue();
		}

		return returnValue;
	}
	
	@Override
	public void destroy()
	{
		rmqSystem.destroy();
	}

	@Override
	protected Stream createStream(StreamDetails details) throws Exception
	{
		return new StreamImpl(rmqSystem.getConnection().createChannel(), details);
	}
}
