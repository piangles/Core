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
import org.piangles.core.util.Logger;
import org.piangles.core.util.abstractions.ConfigProvider;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

public final class FireAndForgetController extends AbstractController
{
	private static final long TEN_SECONDS = 10 * 1000;
	
	private ConfigProvider cp = null;
	private RabbitMQSystem rmqSystem = null;
	private String queueName = null;
	private Channel channel = null;
	private Consumer consumer = null; 
			
	@Override
	public void init() throws ControllerException
	{
		cp = new InMemoryConfigProvider(getServiceName(), getProperties());
	}

	@Override
	public void start() throws ControllerException
	{
		int attemptCount = 0;
		boolean reconnect = true;
		boolean keepLooping = true;
		while (keepLooping)
		{
			if (attemptCount == 0)
			{
				Logger.getInstance().info("FireAndForgetController initializing and making first attempt.");
			}
			try
			{
				if (reconnect)
				{
					attemptCount = attemptCount + 1;
					
					Logger.getInstance().info("Obtaining FireAndForgetController for Service: " + cp.getServiceName() + " AttemptNumber: " + attemptCount);
					rmqSystem = ResourceManager.getInstance().getRabbitMQSystem(cp);
					
					Logger.getInstance().debug("Creating RabbitMQ Channel for Service: " + cp.getServiceName());
					channel = rmqSystem.getConnection().createChannel();
					
					channel.exchangeDeclare(RabbitProps.getTopic(getProperties()), "fanout");
					queueName = channel.queueDeclare().getQueue();
					
					channel.queueBind(queueName, RabbitProps.getTopic(getProperties()), "");
					consumer = new ConsumerImpl(channel);
					
					Logger.getInstance().debug("Starting to Listen for FireAndForget Requests for Service: " + cp.getServiceName());
					reconnect = listen();
					/**
					 * Remember FireAndForget listen will return immediately
					 */
				}
			}
			catch (Exception e)
			{
				Logger.getInstance().warn("Exception in start of FireAndForgetController for Service: " + getServiceName() + ". Reason: " + e.getMessage(), e);
			}
			finally
			{
				try
				{
					Logger.getInstance().info("Going to sleep in FireAndForgetController->start for Service: " + getServiceName());
					Thread.sleep(TEN_SECONDS);

					boolean isConnectionOpen = rmqSystem.getConnection().isOpen();
					boolean isChannelOpen = channel.isOpen();
					boolean isShutdownException = false;
					
					ShutdownSignalException shutdownExpt = rmqSystem.getConnection().getCloseReason();
					if (shutdownExpt != null)
					{
						isShutdownException = ShutdownHelper.process(getServiceName(), getClass().getSimpleName(), shutdownExpt);
					}
					
					if (!isConnectionOpen || !isChannelOpen || isShutdownException)
					{
						Logger.getInstance().info("NeedTo-Reconnect: FireAndForgetController->start for Service: " + getServiceName() + 
													" [isConnectionOpen=" + isConnectionOpen + "]" +
													" [isChannelOpen=" + isChannelOpen + "]" +
													" [isShutdownException=" + isShutdownException + "]"
												);
						reconnect = true;
						ResourceManager.getInstance().close(cp.getComponentId());
					}
					else
					{
						Logger.getInstance().info("Still-Connected: FireAndForgetController->start for Service: " + getServiceName());
					}
				}
				catch (Exception e)
				{
					reconnect = true;
					Logger.getInstance().warn("Exception in start->finally of FireAndForgetController for Service: " + getServiceName() + ". Reason: " + e.getMessage(), e);
				}
			}
		}
	}
	
	@Override
	public void destroy()
	{
		rmqSystem.close();
	}
	
	
	private boolean listen()
	{
		boolean reconnect = false;
		try
		{
			channel.basicConsume(queueName, true, consumer);
		}
		catch (Throwable e)
		{
			Logger.getInstance().error("Exception in FireAndForgetController->listen in RabbitMQChannel->basicConsume. Reason: " + e.getMessage(), e);
			reconnect = true;
		}
		return reconnect;
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
