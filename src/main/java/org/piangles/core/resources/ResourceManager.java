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
			Object resource = componentIdResourceMap.get(cp.getComponentId());
			if (resource == null)
			{
				resource = new RDBMSDataStore(cp.getServiceName(), cp.getProperties());
				componentIdResourceMap.put(cp.getComponentId(), resource);
			}
				
			dataStore = (RDBMSDataStore) resource;
		}
		catch (Exception e)
		{
			throw new ResourceException(e);
		}
		

		return dataStore;
	}
	
	public RDBMSDataStore getRDBMSDataStore(String componentId)
	{
		return (RDBMSDataStore)componentIdResourceMap.get(componentId);		
	}
	
	//Need to add here a MessagingSystem specifically for Kafka
}