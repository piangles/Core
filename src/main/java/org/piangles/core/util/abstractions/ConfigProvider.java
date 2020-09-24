package org.piangles.core.util.abstractions;

import java.util.Properties;

public interface ConfigProvider
{
	public String getServiceName();
	public String getComponentId();
	public Properties getProperties() throws Exception;
}
