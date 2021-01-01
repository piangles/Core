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

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.RpcServer;

public class HelloServer
{
	public static void main(String[] args)
	{
		try
		{
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("ec2-100-25-191-222.compute-1.amazonaws.com");
			factory.setPort(5672);
			factory.setUsername("msgUser");
			factory.setPassword("msgPassword");

			Connection conn = factory.newConnection();
			final Channel channel = conn.createChannel();

			channel.queueDeclare("Hello", false, false, false, null);
			RpcServer server = new RpcServer(channel, "Hello")
			{
				@Override
				public byte[] handleCall(byte[] requestBody, AMQP.BasicProperties replyProperties)
				{
					System.out.println(Thread.currentThread().getName());
					String request = new String(requestBody);
					System.out.println("Got request: " + request);
					String response = "Hello, " + request + "!";
					return response.getBytes();
				}
				
				@Override
				public void handleCast(Delivery delivery)
				{
				}
			};
			server.mainloop();
		}
		catch (Exception ex)
		{
			System.err.println("Main thread caught exception: " + ex);
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
