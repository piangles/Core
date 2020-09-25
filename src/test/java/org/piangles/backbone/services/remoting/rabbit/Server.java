package org.piangles.backbone.services.remoting.rabbit;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class Server
{

	private static final String RPC_QUEUE_NAME = "rpc_queue";

	private static int increment(int n)
	{
		return n+1;
	}

	public static void main(String[] argv)
	{
		Connection connection = null;
		Channel channel = null;
		try
		{
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("ec2-54-172-250-220.compute-1.amazonaws.com");
			factory.setPort(5672);
			factory.setUsername("msgUser");
			factory.setPassword("msgPassword");

			connection = factory.newConnection();
			channel = connection.createChannel();

			channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

			channel.basicQos(1);

			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

			System.out.println(" [x] Awaiting RPC requests");

			while (true)
			{
				String response = null;

				QueueingConsumer.Delivery delivery = consumer.nextDelivery();

				BasicProperties props = delivery.getProperties();
				BasicProperties replyProps = new BasicProperties.Builder().correlationId(props.getCorrelationId()).build();

				try
				{
					String message = new String(delivery.getBody(), "UTF-8");
					int n = Integer.parseInt(message);

					System.out.println(" [.] increment(" + message + ")");
					response = "" + increment(n);
				}
				catch (Exception e)
				{
					System.out.println(" [.] " + e.toString());
					response = "";
				}
				finally
				{
					System.out.println("ReplyTo " + props.getReplyTo());
					channel.basicPublish("", props.getReplyTo(), replyProps, response.getBytes("UTF-8"));

					channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (Exception ignore)
				{
				}
			}
		}
	}
}