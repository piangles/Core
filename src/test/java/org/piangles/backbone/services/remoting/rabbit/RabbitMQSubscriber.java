package org.piangles.backbone.services.remoting.rabbit;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RabbitMQSubscriber
{
	private static final String EXCHANGE_NAME = "logs";

	public static void main(String[] argv)
	{
		try
		{
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("ec2-54-172-250-220.compute-1.amazonaws.com");
			factory.setPort(5672);
			factory.setUsername("msgUser");
			factory.setPassword("msgPassword");
			
			
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, EXCHANGE_NAME, "");

			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

			Consumer consumer = new DefaultConsumer(channel)
			{
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
				{
					String message = new String(body, "UTF-8");
					System.out.println(" [x] Received '" + message + "'");
				}
			};
			channel.basicConsume(queueName, true, consumer);
		}
		catch (IOException | TimeoutException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
