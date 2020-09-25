package org.piangles.core.services.remoting.rabbit;

import java.util.Properties;

public final class RMQProperties
{
	private static final String HOST = "RMQHostName";
	private static final String PORT = "RMQPort";
	public static final String LOGIN = "RMQLoginId";
	public static final String PASSWORD = "RMQPassword";
	public static final String TOPIC = "Topic";
	private static final String TIMEOUT = "TimeOut";
	
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

	public String getTopic()
	{
		return properties.getProperty(TOPIC);
	}
	
	public long getTimeout()
	{
		return Long.valueOf(properties.getProperty(TIMEOUT));
	}
}
