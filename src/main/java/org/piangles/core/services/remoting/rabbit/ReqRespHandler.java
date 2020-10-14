package org.piangles.core.services.remoting.rabbit;

import java.lang.reflect.Method;
import java.util.UUID;

import org.piangles.core.services.Request;
import org.piangles.core.services.Response;
import org.piangles.core.services.remoting.handlers.AbstractHandler;
import org.piangles.core.services.remoting.handlers.HandlerException;
import org.piangles.core.util.coding.JAVA;

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
			if (delivery != null)
			{
				if (delivery.getProperties().getCorrelationId().equals(corrId))
				{
					responseAsBytes = delivery.getBody();
				}
				else
				{
					returnValue = new RuntimeException(endpoint(method) + " received a unexpected corelationId.");
				}
				break;
			}
			else //Timedout
			{
				returnValue = new RuntimeException(endpoint(method) + " timed out.");
				break;
			}
		}
		
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
