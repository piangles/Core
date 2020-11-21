package org.piangles.core.resources;

import java.util.Properties;

final class RMQProperties
{
	private static final String HOST = "RMQHostName";
	private static final String PORT = "RMQPort";
	static final String LOGIN = "RMQLoginId";
	static final String PASSWORD = "RMQPassword";
	
	private Properties properties = null;
	
	public RMQProperties(Properties properties)
	{
		this.properties = properties;
	}

	public String getHost()
	{
		return properties.getProperty(HOST);
	}

	public int getPort()
	{
		return Integer.valueOf(properties.getProperty(PORT));
	}

	public String getLogin()
	{
		return properties.getProperty(LOGIN);
	}

	public String getPassword()
	{
		return properties.getProperty(PASSWORD);
	}
}
