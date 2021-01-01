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
import com.rabbitmq.client.RpcClient;
import com.rabbitmq.client.RpcClientParams;

public class HelloClient
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
			Channel channel = conn.createChannel();
			//ch.queueDeclare("Hello", false, false, false, null);
			
			String corrId = UUID.randomUUID().toString();
			
			String replyQueueName = channel.queueDeclare().getQueue();
			BasicProperties props = new BasicProperties.Builder()
										.correlationId(corrId)
										.replyTo(replyQueueName)
										.build();

			
			RpcClientParams params = new RpcClientParams().
					channel(channel).
					exchange("").
					routingKey("Hello").
					timeout(5000);

			RpcClient service = new RpcClient(params);

			for (int i=0; i < 25; ++i)
			{
				String message = "" + i;
				System.out.println(new String(service.primitiveCall(props, message.getBytes())));
			}
			conn.close();
		}
		catch (Exception e)
		{
			System.err.println("Main thread caught exception: " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}
}
