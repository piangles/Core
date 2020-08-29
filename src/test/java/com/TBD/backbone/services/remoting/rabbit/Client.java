package com.TBD.backbone.services.remoting.rabbit;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;
import java.util.UUID;

public class Client
{

	private Connection connection;
	private Channel channel;
	private String requestQueueName = "rpc_queue";
	private String replyQueueName;
	private QueueingConsumer consumer;

	public Client() throws Exception
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("ec2-54-172-250-220.compute-1.amazonaws.com");
		factory.setPort(5672);
		factory.setUsername("msgUser");
		factory.setPassword("msgPassword");
		
		connection = factory.newConnection();
		channel = connection.createChannel();

		replyQueueName = channel.queueDeclare().getQueue();
		consumer = new QueueingConsumer(channel);
		channel.basicConsume(replyQueueName, true, consumer);
	}

	public String call(String message) throws Exception
	{
		String response = null;
		String corrId = UUID.randomUUID().toString();
		System.out.println("corrId : " + corrId);
		System.out.println("replyQueueName : " + replyQueueName);
		BasicProperties props = new BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName).build();

		channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

		while (true)
		{
			QueueingConsumer.Delivery delivery = consumer.nextDelivery(1000);
			if (delivery.getProperties().getCorrelationId().equals(corrId))
			{
				response = new String(delivery.getBody(), "UTF-8");
				break;
			}
		}

		return response;
	}

	public void close() throws Exception
	{
		connection.close();
	}

	public static void main(String[] argv)
	{
		Client incrementService = null;
		String response = null;
		try
		{
			incrementService = new Client();

			System.out.println(" [x] Requesting fib(30)");
			response = incrementService.call("31");
			System.out.println(" [.] Got '" + response + "'");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (incrementService != null)
			{
				try
				{
					incrementService.close();
				}
				catch (Exception ignore)
				{
				}
			}
		}
	}
}