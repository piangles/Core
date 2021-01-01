/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.backbone.services.remoting.rabbit;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Delivery;

public class Server
{

	private static final String RPC_QUEUE_NAME = "rpc_queue";

	private static int increment(int n)
	{
		return n + 1;
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

			Consumer consumer = new DefaultConsumer(channel);
			channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

			System.out.println(" [x] Awaiting RPC requests");

			while (true)
			{
				String response = null;

				Delivery delivery = null; //consumer.nextDelivery();

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
