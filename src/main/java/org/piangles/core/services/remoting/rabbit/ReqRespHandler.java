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

import java.util.UUID;

import org.piangles.core.resources.RabbitMQSystem;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.services.Request;
import org.piangles.core.services.Response;
import org.piangles.core.services.remoting.handlers.AbstractHandler;
import org.piangles.core.services.remoting.handlers.HandlerException;
import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamDetails;
import org.piangles.core.util.InMemoryConfigProvider;
import org.piangles.core.util.coding.JAVA;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.RpcClient;
import com.rabbitmq.client.RpcClientParams;

public final class ReqRespHandler extends AbstractHandler
{
	private RabbitMQSystem rmqSystem = null;
	
	@Override
	protected void init() throws HandlerException
	{
		try
		{
			rmqSystem = ResourceManager.getInstance().getRabbitMQSystem(new InMemoryConfigProvider(getServiceName(), getProperties()));
		}
		catch (Exception e)
		{
			throw new HandlerException(e);
		}

	}

	@Override
	protected Response processRequest(Request request) throws Throwable
	{
		String corrId = UUID.randomUUID().toString();
		Channel channel = rmqSystem.getConnection().createChannel();
		
		/**
		 * TODO exchange will have to derived from Topic eventually for load balancing.
		 *
		 * This is not neeeded any more, the replyTo is defaulted in BasicProperties
		 * String replyQueueName = channel.queueDeclare().getQueue();
		 * BasicProperties.replyTo(replyQueueName)
		 * queueDeclare creates a responseQueue which is nondurable and autodeleted
		 */
		String exchange = "";
		
		BasicProperties props = new BasicProperties.Builder()
									.correlationId(corrId)
									.build();

		RpcClientParams params = new RpcClientParams().
									channel(channel).
									exchange(exchange).
									routingKey(RabbitProps.getTopic(getProperties())).
									timeout((int)RabbitProps.getTimeout(getProperties()));
		
		RpcClient rpcClient = new RpcClient(params);
		byte[] responseAsBytes = rpcClient.doCall(props, getEncoder().encode(request)).getBody();
		rpcClient.close();
		channel.close();
		
		Response response = null;
		if (responseAsBytes != null)
		{
			response = JAVA.getDecoder().decode(responseAsBytes, Response.class);
		}

		return response;
	}
	
	@Override
	public void destroy()
	{
		rmqSystem.destroy();
	}

	@Override
	protected Stream<?> createStream(StreamDetails details) throws Exception
	{
		return new StreamImpl<>(getServiceName(), getProperties(), rmqSystem.getConnection().createChannel(), details);
	}
}
