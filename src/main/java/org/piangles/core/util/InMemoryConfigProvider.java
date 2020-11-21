package org.piangles.core.util;

import java.util.Properties;
import java.util.UUID;

import org.piangles.core.util.abstractions.AbstractConfigProvider;

public class InMemoryConfigProvider extends AbstractConfigProvider
{
	private Properties props = null;
	
	public InMemoryConfigProvider(String serviceName, Properties props)
	{
		this(serviceName, UUID.randomUUID().toString(), props);
	}

	public InMemoryConfigProvider(String serviceName, String componentId, Properties props)
	{
		super(serviceName, componentId);
		this.props = props;
	}

	@Override
	public Properties getProperties() throws Exception
	{
		return props;
	}
}
