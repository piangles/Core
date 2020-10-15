package org.piangles.core.services.remoting.rabbit;

import java.lang.reflect.Method;
import java.util.UUID;

import org.piangles.core.services.Request;
import org.piangles.core.services.Response;
import org.piangles.core.services.remoting.handlers.AbstractHandler;
import org.piangles.core.services.remoting.handlers.HandlerException;
import org.piangles.core.util.coding.JAVA;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.RpcClient;
import com.rabbitmq.client.RpcClientParams;

public final class ReqRespHandler extends AbstractHandler
{
	private RMQHelper rmqHelper = null;
	
	public ReqRespHandler(String serviceName)
	{
		super(serviceName);
	}

	@Override
	protected void init() throws HandlerException
	{
		try
		{
			rmqHelper = new RMQHelper(getServiceName(), false, getProperties());
		}
		catch (Exception e)
		{
			throw new HandlerException(e);
		}

	}

	@Override
	protected Object processMethodCall(Method method, Object[] args) throws Throwable
	{
		Object returnValue = null;

		Request request = createRequest(method, args);

		String corrId = UUID.randomUUID().toString();
		Channel channel = rmqHelper.getConnection().createChannel();
		
		String replyQueueName = channel.queueDeclare().getQueue();
		BasicProperties props = new BasicProperties.Builder()
									.correlationId(corrId)
									.replyTo(replyQueueName)
									.build();

		RpcClientParams params = new RpcClientParams().
									channel(channel).
									exchange("").
									routingKey(rmqHelper.getRMQProperties().getTopic()).
									timeout((int)rmqHelper.getRMQProperties().getTimeout());
		RpcClient rpcClient = new RpcClient(params);
		byte[] responseAsBytes = rpcClient.doCall(props, rmqHelper.getEncoder().encode(request)).getBody();
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
		rmqHelper.destroy();
	}
}
