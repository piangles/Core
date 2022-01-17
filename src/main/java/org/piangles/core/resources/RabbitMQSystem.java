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
 
 
 
package org.piangles.core.resources;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.piangles.core.util.abstractions.Decrypter;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public final class RabbitMQSystem implements Resource
{
	private static final String AMQPS = "amqps://";
	private static final String DECRYPTER_CLASS_NAME = "DecrypterClassName";
	private static final String DECRYPTER_AUTHZ_ID = "DecrypterAuthorizationId";

	private String serviceName = null;
	private Connection connection = null;
	
	RabbitMQSystem(String serviceName, Properties properties) throws Exception
	{
		this.serviceName = serviceName;
		
		RMQProperties rmqProperties = createRMQProperties(properties);
		
		ConnectionFactory factory = new ConnectionFactory();
		if (StringUtils.startsWithIgnoreCase(rmqProperties.getHost(), AMQPS))
		{
			factory.setUri(rmqProperties.getHost());
		}
		else
		{
			factory.setHost(rmqProperties.getHost());
		}
		factory.setPort(rmqProperties.getPort());
		factory.setUsername(rmqProperties.getLogin());
		factory.setPassword(rmqProperties.getPassword());

		/**
		 * The following is needed to Keep connection Alive during inactivity with
		 * the server.
		 * https://www.rabbitmq.com/heartbeats.html
		 * https://www.rabbitmq.com/api-guide.html#recovery
		 */
		factory.setRequestedHeartbeat(rmqProperties.getRequestedHeartbeat());
		factory.setAutomaticRecoveryEnabled(rmqProperties.isAutomaticRecoveryEnabled());
		
		connection = factory.newConnection();
	}

	@Override
	public void close() throws Exception
	{
		connection.close();
	}

	public Connection getConnection()
	{
		return connection;
	}
	
	public void destroy()
	{
		try
		{
			connection.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private  RMQProperties createRMQProperties(Properties props) throws Exception
	{
		try
		{
			Decrypter decrypter = null;

			String decrypterClassName = props.getProperty(DECRYPTER_CLASS_NAME);
			String decrypterAuthorizationIdName = DECRYPTER_AUTHZ_ID;
			String decrypterAuthorizationId = props.getProperty(DECRYPTER_AUTHZ_ID);

			if (decrypterClassName != null)
			{
				decrypter = (Decrypter)Class.forName(decrypterClassName).getConstructor().newInstance();
			}
			
			if (decrypter != null)
			{
				decrypter.init(serviceName, "Discovery", decrypterAuthorizationIdName, decrypterAuthorizationId);
				
				props.setProperty(RMQProperties.LOGIN, decrypter.decrypt(RMQProperties.LOGIN, props.getProperty(RMQProperties.LOGIN)));
				props.setProperty(RMQProperties.PASSWORD, decrypter.decrypt(RMQProperties.PASSWORD, props.getProperty(RMQProperties.PASSWORD)));
			}
		}
		catch (Exception e)
		{
			throw new Exception(e);
		}
		
		return new RMQProperties(props);
	}
}
