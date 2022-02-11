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
import org.piangles.core.resources.ResourceException;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.services.remoting.RequestProcessingThread;
import org.piangles.core.services.remoting.controllers.AbstractController;
import org.piangles.core.services.remoting.controllers.ControllerException;
import org.piangles.core.util.InMemoryConfigProvider;
import org.piangles.core.util.Logger;
import org.piangles.core.util.abstractions.ConfigProvider;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.RpcServer;
import com.rabbitmq.client.ShutdownSignalException;

public final class ReqRespController extends AbstractController
{
	private ConfigProvider cp = null;
	private RabbitMQSystem rmqSystem = null;
	private Channel channel = null;
	private RpcServer server = null;

	@Override
	public void init() throws ControllerException
	{
		cp = new InMemoryConfigProvider(getServiceName(), getProperties());
	}

	@Override
	public void start() throws ControllerException
	{
		int attemptCount = 0;
		boolean keepListening = true;
		
		while ((attemptCount == 0) || keepListening)
		{
			if (attemptCount == 0)
			{
				Logger.getInstance().info("ReqRespController initializing and making first attempt.");
			}
			attemptCount = attemptCount + 1;
			
			try
			{
				Logger.getInstance().info("Obtaining RabbitMQSystem for Service: " + cp.getServiceName() + " AttemptNumber: " + attemptCount);
				rmqSystem = ResourceManager.getInstance().getRabbitMQSystem(cp);

				Logger.getInstance().debug("Creating RabbitMQ Channel for Service: " + cp.getServiceName());
				channel = rmqSystem.getConnection().createChannel();
				
				Logger.getInstance().debug("Starting to Listen for Requests for Service: " + cp.getServiceName());
				keepListening = listen();
				/**
				 * This should ideally never return and if it does we need to 
				 * reconnect if listen returns true.
				 */
			}
			catch(Throwable e)
			{
				Logger.getInstance().warn("Exception in start of ReqRespController for Service: " + getServiceName() + ". Reason: " + e.getMessage(), e);
			}
			finally
			{
				try
				{
					ResourceManager.getInstance().getRabbitMQSystem(cp).close();				
					Thread.sleep(1000);
				}
				catch (Exception e)
				{
					Logger.getInstance().warn("Exception in start->finally of ReqRespController for Service: " + getServiceName() + ". Reason: " + e.getMessage(), e);
				}
			}
		}
		
		Logger.getInstance().warn("Breaking out of the startLoop-> ReqRespController for Service: " + getServiceName());
	}
	
	@Override
	protected boolean isStopRequested()
	{
		Exception e = new Exception("Dummy Exception purely to identify the StackTrace of isStopRequested."); 
		Logger.getInstance().warn("ReqRespController for Service: " + getServiceName() + " isStopRequested called. Location: " + e.getMessage(), e);
		Logger.getInstance().warn("ReqRespController for Service: " + getServiceName() + " RabbitMQ->RpcServer->terminateMainloop");
		server.terminateMainloop();
		return super.isStopRequested();
	}

	@Override
	public void destroy()
	{
		try
		{
			ResourceManager.getInstance().getRabbitMQSystem(cp).close();
		}
		catch (ResourceException e)
		{
			Logger.getInstance().warn("ResourceException during destroy of ReqRespController for Service: " + cp.getServiceName() + ". Reason: " + e.getMessage(), e);
		}
	}
	
	private boolean listen() throws ControllerException
	{
		boolean keepListening = true;
		try
		{
			/**
			 * This needs to come from configuration this will help in LoadBalacing
			 * even if we multithread if it is at 1.
			 * 
			 * RabbitMQ will not send a message here till we send an Ack back.
			 * 
			 * AMQP 0-9-1 specifies the basic.qos method to make it possible to limit the number of unacknowledged messages 
			 * on a channel (or connection) when consuming (aka "prefetch count").
			 * 
			 * https://www.rabbitmq.com/consumer-prefetch.html
			 * https://www.rabbitmq.com/tutorials/tutorial-two-java.html
			 */
			channel.basicQos(1);// accept only one unack-ed message at a time
			
			boolean durable = false; 
			boolean exclusive = false; 
			boolean autoDelete = false;
			
			channel.queueDeclare(RabbitProps.getTopic(getProperties()), durable, exclusive, autoDelete, null);
			
			server = new RpcServer(channel, RabbitProps.getTopic(getProperties()))
			{
				@Override
				public void processRequest(Delivery delivery) throws IOException
				{
					try
					{
						RequestProcessingThread rpt = new RequestProcessingThread(
								getServiceName(), getService(),
								getPreApprovedSessionId(), getSessionValidator(),
								getEncoder(), getDecoder(), 
								delivery.getBody(), new ResponseSenderImpl(getServiceName(), getProperties(), rmqSystem, channel, delivery));
						
						rpt.start();
					}
					catch(Throwable t)
					{
						Logger.getInstance().error("Exception in ReqRespController RpcServer->processRequest for Service: " + getServiceName() + ". Reason: " + t.getMessage(), t);		
					}
				}
			};
			
			/**
			 * Once the mainLoop starts it does not return the call.
			 */
			ShutdownSignalException shutdownExpt = server.mainloop();
			keepListening = ShutdownHelper.process(getServiceName(), getClass().getSimpleName(), shutdownExpt);
		}
		catch (Throwable e)
		{
			Logger.getInstance().error("Exception in ReqRespController in RabbitMQ->mainloop. Reason: " + e.getMessage(), e);
			keepListening = true;
		}
		
		return keepListening;
	}
}
