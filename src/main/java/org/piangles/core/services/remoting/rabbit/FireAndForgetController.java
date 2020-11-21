package org.piangles.core.services.remoting.rabbit;

import java.io.IOException;

import org.piangles.core.resources.RabbitMQSystem;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.services.remoting.RequestProcessorThread;
import org.piangles.core.services.remoting.controllers.AbstractController;
import org.piangles.core.services.remoting.controllers.ControllerException;
import org.piangles.core.util.InMemoryConfigProvider;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public final class FireAndForgetController extends AbstractController
{
	private RabbitMQSystem rmqSystem = null;
	private String queueName = null;
	private Channel channel = null;
	private Consumer consumer = null; 
			
	@Override
	public void init() throws ControllerException
	{
		try
		{
			rmqSystem = ResourceManager.getInstance().getRabbitMQSystem(new InMemoryConfigProvider(getServiceName(), getProperties()));
			channel = rmqSystem.getConnection().createChannel();
			
			channel.exchangeDeclare(RabbitProps.getTopic(getProperties()), "fanout");
			queueName = channel.queueDeclare().getQueue();
			
			channel.queueBind(queueName, RabbitProps.getTopic(getProperties()), "");
			consumer = new ConsumerImpl(channel);	
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
			channel.basicConsume(queueName, true, consumer);
		}
		catch (IOException e)
		{
			throw new ControllerException(e);
		}
	}
	
	@Override
	public void destroy()
	{
		rmqSystem.destroy();
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
			RequestProcessorThread rpt = new RequestProcessorThread(
															getServiceName(), getService(), 
															FireAndForgetController.this.getPreDeterminedSessionId(), getSessionValidator(),
															getEncoder(), getDecoder(),
															body, null);
			rpt.start();
		}
	}
}
