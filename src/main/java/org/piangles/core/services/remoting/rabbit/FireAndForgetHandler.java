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

import org.piangles.core.resources.RabbitMQSystem;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.services.Request;
import org.piangles.core.services.remoting.handlers.AbstractHandler;
import org.piangles.core.services.remoting.handlers.HandlerException;
import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamDetails;
import org.piangles.core.util.InMemoryConfigProvider;

import com.rabbitmq.client.Channel;

public final class FireAndForgetHandler extends AbstractHandler
{
	private RabbitMQSystem rmqSystem = null;
	private Channel channel = null;
	
	@Override
	protected void init() throws HandlerException
	{
		try
		{
			rmqSystem = ResourceManager.getInstance().getRabbitMQSystem(new InMemoryConfigProvider(getServiceName(), getProperties()));
			
			channel = rmqSystem.getConnection().createChannel();
			channel.exchangeDeclare(RabbitProps.getTopic(getProperties()), "fanout");
		}
		catch (Exception e)
		{
			throw new HandlerException(e);
		}
	}

	@Override
	public Object processRequest(Request request) throws Throwable
	{
		//TODO: Make this into a Future or a Separate Thread
		channel.basicPublish(RabbitProps.getTopic(getProperties()), "", null, getEncoder().encode(request));
		return null;
	}
	
	@Override
	public void destroy()
	{
		rmqSystem.destroy();
	}

	@Override
	protected Stream<?> createStream(StreamDetails details) throws Exception
	{
		throw new Exception("Streaming is not supported for FireAndForget protocol");
	}
}
