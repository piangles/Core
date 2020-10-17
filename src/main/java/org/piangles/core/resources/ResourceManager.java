package org.piangles.core.resources;

import java.util.HashMap;

import org.piangles.core.util.abstractions.ConfigProvider;


public class ResourceManager
{
	private static ResourceManager self = null;
	private static HashMap<String, Object> componentIdResourceMap = null;

	private ResourceManager()
	{
		componentIdResourceMap = new HashMap<String, Object>();
	}

	public static ResourceManager getInstance()
	{
		if (self == null)
		{
			self = new ResourceManager();
		}

		return self;
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
		return null;
	}
	
	public DistributedCache getDistributedCache()
	{
		return null;
	}
	
	public NoSQLDataStore getNoSQLDataStore()
	{
		return null;
	}
}