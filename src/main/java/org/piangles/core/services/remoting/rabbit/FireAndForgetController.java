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
 
 
 
package org.piangles.core.services.remoting.rabbit;

import java.io.IOException;

import org.piangles.core.resources.RabbitMQSystem;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.services.remoting.RequestProcessingThread;
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
		rmqSystem.close();
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
			RequestProcessingThread rpt = new RequestProcessingThread(
															getServiceName(), getService(), 
															FireAndForgetController.this.getPreApprovedSessionId(), getSessionValidator(),
															getEncoder(), getDecoder(),
															body, null);
			rpt.start();
		}
	}
}
