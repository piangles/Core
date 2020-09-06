package com.TBD.core.util.central;

import java.util.Properties;

import com.TBD.core.util.abstractions.AbstractConfigProvider;

public class CentralConfigProvider  extends AbstractConfigProvider
{
	public CentralConfigProvider(String serviceName, String componentId)
	{
		super(serviceName, componentId);
	}

	@Override
	public Properties getProperties() throws Exception
	{
		return CentralClient.tier1Config(getComponentId());
	}
}
