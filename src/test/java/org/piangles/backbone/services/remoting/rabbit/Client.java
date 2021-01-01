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

import java.util.UUID;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Delivery;

public class Client
{

	private Connection connection;
	private Channel channel;
	private String requestQueueName = "rpc_queue";
	private String replyQueueName;
	private Consumer consumer;

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
		consumer = new DefaultConsumer(channel);
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
			Delivery delivery = null; //consumer.nextDelivery(1000);
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
