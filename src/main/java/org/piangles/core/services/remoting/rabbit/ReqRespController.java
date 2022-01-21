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

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.RpcServer;
import com.rabbitmq.client.ShutdownSignalException;

public final class ReqRespController extends AbstractController
{
	private RabbitMQSystem rmqSystem = null;
	private Channel channel = null;
	private RpcServer server = null;

	@Override
	public void init() throws ControllerException
	{
		try
		{
			rmqSystem = ResourceManager.getInstance().getRabbitMQSystem(new InMemoryConfigProvider(getServiceName(), getProperties()));

			channel = rmqSystem.getConnection().createChannel();
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
			ShutdownSignalException exception = server.mainloop();
			
			if (exception != null)
			{
				String reference = "No Reference Given.";
				if (exception.getReference() != null)
				{
					reference = exception.getReference().getClass().getCanonicalName();
				}
			
				Logger.getInstance().warn("ReqRespController for Service: " + getServiceName() 
				+ " has exited RabbitMQ->RpcServer. Reason: " + exception.getMessage()
				+ " isHardError: " + exception.isHardError()
				+ " isInitiatedByApplication: " + exception.isInitiatedByApplication()
				+ " Reference: " + reference, exception);
				
				if (exception.getReason() != null)
				{
					Logger.getInstance().warn("ShutdownSignalException for Service: " + getServiceName() + ". Reason: " 
							+ " protocolClassId:" + exception.getReason().protocolClassId() 
							+ " protocolMethodId:" + exception.getReason().protocolMethodId()
							+ " protocolMethodName:" + exception.getReason().protocolMethodName());
				}
				else
				{
					Logger.getInstance().warn("ShutdownSignalException for Service: " + getServiceName() + ". Without a Reason."); 
				}
			}
			else
			{
				Logger.getInstance().warn("ShutdownSignalException for Service: " + getServiceName() + ". is null.");
			}
		}
		catch (Throwable e)
		{
			Logger.getInstance().error("Exception in ReqRespController in RabbitMQ->mainloop. Reason: " + e.getMessage(), e);
			throw new ControllerException(e.getMessage(), e);
		}
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
		rmqSystem.destroy();
	}
}
