package com.TBD.core.util.central;

import java.util.Properties;

import com.TBD.core.util.abstractions.ConfigProvider;

public class CentralConfigProvider  implements ConfigProvider
{
	private String componentId = null;
			
	public CentralConfigProvider(String componentId)
	{
		this.componentId = componentId;
	}

	@Override
	public String getComponentId()
	{
		return componentId;
	}

	@Override
	public Properties getProperties() throws Exception
	{
		return CentralClient.tier1Config(componentId);
	}

}
