package org.piangles.core.services.remoting.rabbit;

import java.io.IOException;
import java.util.Properties;

import org.piangles.core.services.remoting.controllers.AbstractController;
import org.piangles.core.services.remoting.controllers.ControllerException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public final class FireAndForgetController extends AbstractController
{
	private RMQHelper rmqHelper = null;
	private String queueName = null;
	private Consumer consumer = null; 
			
	@Override
	public void init(Properties properties) throws ControllerException
	{
		try
		{
			rmqHelper = new RMQHelper(getServiceName(), true, properties);
			
			rmqHelper.getChannel().exchangeDeclare(rmqHelper.getRMQProperties().getTopic(), "fanout");
			queueName = rmqHelper.getChannel().queueDeclare().getQueue();
			rmqHelper.getChannel().queueBind(queueName, rmqHelper.getRMQProperties().getTopic(), "");
			consumer = new ConsumerImpl(rmqHelper.getChannel());	
		}
		catch (Exception e)
		{
			throw new ControllerException(e);
		}
	}

	@Override
	public void start() throws ControllerException
	{
		try
		{
			rmqHelper.getChannel().basicConsume(queueName, true, consumer);
		}
		catch (IOException e)
		{
			throw new ControllerException(e);
		}
	}
	
	@Override
	public void destroy()
	{
		rmqHelper.destroy();
	}
	
	class ConsumerImpl extends DefaultConsumer
	{
		public ConsumerImpl(Channel channel)
		{
			super(channel);
		}
		
		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
		{
			RequestProcessorThread rpt = new RequestProcessorThread(getServiceName(), getService(), getSessionValidator(), envelope, body, rmqHelper, null);
			rpt.start();
		}
	}
}
