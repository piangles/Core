package org.piangles.core.util.abstractions;

public abstract class AbstractConfigProvider implements ConfigProvider
{
	private String serviceName;
	private String componentId;

	public AbstractConfigProvider(String serviceName, String componentId)
	{
		this.serviceName = serviceName;
		this.componentId = componentId;
	}

	@Override
	public final String getServiceName()
	{
		return serviceName;
	}

	@Override
	public final String getComponentId()
	{
		return componentId;
	}
}
