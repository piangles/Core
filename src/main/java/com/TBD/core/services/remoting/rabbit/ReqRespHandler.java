package com.TBD.core.services.remoting.rabbit;

import java.lang.reflect.Method;
import java.util.UUID;

import com.TBD.core.services.Request;
import com.TBD.core.services.Response;
import com.TBD.core.util.coding.JAVA;
import com.TBD.core.services.remoting.handlers.AbstractHandler;
import com.TBD.core.services.remoting.handlers.HandlerException;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.QueueingConsumer;

public final class ReqRespHandler extends AbstractHandler
{
	private RMQHelper rmqHelper = null;
	
	private String requestQueueName = null;
	private String replyQueueName = null;
	private QueueingConsumer consumer;
	
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
			
			requestQueueName = rmqHelper.getRMQProperties().getTopic();
			replyQueueName = rmqHelper.getChannel().queueDeclare().getQueue();
			
			consumer = new QueueingConsumer(rmqHelper.getChannel());
			rmqHelper.getChannel().basicConsume(replyQueueName, true, consumer);
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
		BasicProperties props = new BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName).build();

		rmqHelper.getChannel().basicPublish("", requestQueueName, props, rmqHelper.getEncoder().encode(request));

		byte[] responseAsBytes = null;
		while (true)
		{
			QueueingConsumer.Delivery delivery = consumer.nextDelivery(rmqHelper.getRMQProperties().getTimeout());
			//Since this is request/response as soon as we get the first message , break.
			if (delivery != null && delivery.getProperties().getCorrelationId().equals(corrId))
			{
				responseAsBytes = delivery.getBody();
				break;
			}
		}
		
		Response response = null;
		if (responseAsBytes != null)
		{
			response = JAVA.getDecoder().decode(responseAsBytes, Response.class);
			returnValue = response.getReturnValue();
		}
		else
		{
			returnValue = new RuntimeException(getServiceName() + " timed out.");
		}

		return returnValue;
	}
	
	@Override
	public void destroy()
	{
		rmqHelper.destroy();
	}
}
