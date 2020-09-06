package com.TBD.core.util.abstractions;

public abstract class AbstractConfigProvider implements ConfigProvider
{
	private String serviceName;
	private String componentId;

	
	public AbstractConfigProvider(String serviceName, String componentId) {
		super();
		this.serviceName = serviceName;
		this.componentId = componentId;
	}

	@Override
	public final String getServiceName() {
		return serviceName;
	}

	@Override
	public final String getComponentId() {
		return componentId;
	}
}
