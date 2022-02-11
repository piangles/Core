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

import java.util.HashMap;
import java.util.Map;

import org.piangles.core.util.abstractions.ConfigProvider;


public final class ResourceManager
{
	private static ResourceManager self = null;

	private static Map<String, Resource> componentIdResourceMap = null;

	private ResourceManager()
	{
		componentIdResourceMap = new HashMap<>();
	}

	public static ResourceManager getInstance()
	{
		if (self == null)
		{
			self = new ResourceManager();
		}

		return self;
	}
	
	public synchronized void close()
	{
		for (String componentId : componentIdResourceMap.keySet())
		{
			try
			{
				componentIdResourceMap.get(componentId).close();
			}
			catch (Exception e)
			{
				System.err.println("Exception while closing ResourceId: " + componentId);
				e.printStackTrace(System.err);
			}
		}
		componentIdResourceMap.clear();
	}
	
	public synchronized void close(String componentId)
	{
		try
		{
			componentIdResourceMap.remove(componentId).close();
		}
		catch (Exception e)
		{
			System.err.println("Exception while closing ResourceId: " + componentId);
			e.printStackTrace(System.err);
		}
	}
	
	public RDBMSDataStore getRDBMSDataStore(ConfigProvider cp) throws ResourceException
	{
		RDBMSDataStore dataStore = null;

		try
		{
			dataStore = (RDBMSDataStore)componentIdResourceMap.get(cp.getComponentId());
			if (dataStore == null)
			{
				dataStore = new RDBMSDataStore(cp.getServiceName(), cp.getProperties());
				componentIdResourceMap.put(cp.getComponentId(), dataStore);
			}
		}
		catch (Exception e)
		{
			throw new ResourceException(e);
		}
		
		return dataStore;
	}

	public MongoDataStore getMongoDataStore(ConfigProvider cp) throws ResourceException
	{
		MongoDataStore dataStore = null;

		try
		{
			dataStore = (MongoDataStore)componentIdResourceMap.get(cp.getComponentId());
			if (dataStore == null)
			{
				dataStore = new MongoDataStore(cp.getServiceName(), cp.getProperties());
				componentIdResourceMap.put(cp.getComponentId(), dataStore);
			}
		}
		catch (Exception e)
		{
			throw new ResourceException(e);
		}
		
		return dataStore;
	}

	public RedisCache getRedisCache(ConfigProvider cp) throws ResourceException
	{
		RedisCache redisCache = null;

		try
		{
			redisCache = (RedisCache)componentIdResourceMap.get(cp.getComponentId());
			if (redisCache == null)
			{
				redisCache = new RedisCache(cp.getServiceName(), cp.getProperties());
				componentIdResourceMap.put(cp.getComponentId(), redisCache);
			}
		}
		catch (Exception e)
		{
			throw new ResourceException(e);
		}
		
		return redisCache;
	}

	public KafkaMessagingSystem getKafkaMessagingSystem(ConfigProvider cp) throws ResourceException
	{
		KafkaMessagingSystem msgSystem = null;

		try
		{
			msgSystem = (KafkaMessagingSystem)componentIdResourceMap.get(cp.getComponentId());
			if (msgSystem == null)
			{
				msgSystem = new KafkaMessagingSystem(cp.getServiceName(), cp.getProperties());
				componentIdResourceMap.put(cp.getComponentId(), msgSystem);
			}
		}
		catch (Exception e)
		{
			throw new ResourceException(e);
		}

		return msgSystem;
	}
	
	public RabbitMQSystem getRabbitMQSystem(ConfigProvider cp) throws ResourceException
	{
		RabbitMQSystem msgSystem = null;

		try
		{
			msgSystem = (RabbitMQSystem)componentIdResourceMap.get(cp.getComponentId());
			if (msgSystem == null)
			{
				msgSystem = new RabbitMQSystem(cp.getServiceName(), cp.getProperties());
				componentIdResourceMap.put(cp.getComponentId(), msgSystem);
			}
		}
		catch (Exception e)
		{
			throw new ResourceException(e);
		}

		return msgSystem;
	}
}
