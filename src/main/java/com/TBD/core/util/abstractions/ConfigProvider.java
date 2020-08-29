package com.TBD.core.util.abstractions;

import java.util.Properties;

public interface ConfigProvider
{
	public String getComponentId();
	public Properties getProperties() throws Exception;
}
