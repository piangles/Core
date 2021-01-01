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
			 * even if we multithread if it is at 1. RabbitMQ will not send a message
			 * here till we send an Ack back.
			 */
			channel.basicQos(1);
			boolean durable = false; 
			boolean exclusive = false; 
			boolean autoDelete = false;
			channel.queueDeclare(RabbitProps.getTopic(getProperties()), durable, exclusive, autoDelete, null);
			
			server = new RpcServer(channel, RabbitProps.getTopic(getProperties()))
			{
				@Override
				public void processRequest(Delivery delivery) throws IOException
				{
					RequestProcessingThread rpt = new RequestProcessingThread(
															getServiceName(), getService(),
															getPreApprovedSessionId(), getSessionValidator(),
															getEncoder(), getDecoder(), 
															delivery.getBody(), new ResponseSenderImpl(getServiceName(), getProperties(), rmqSystem, channel, delivery));
					rpt.start();
				}
			};
			ShutdownSignalException exception = server.mainloop();
			//TODO Need to handle this
		}
		catch (IOException e)
		{
			throw new ControllerException(e.getMessage(), e);
		}
	}

	@Override
	protected boolean isStopRequested()
	{
		server.terminateMainloop();
		return super.isStopRequested();
	}

	@Override
	public void destroy()
	{
		rmqSystem.destroy();
	}
}
