package org.piangles.core.services.remoting.rabbit;

import java.util.Properties;

final class RabbitProps
{
	private static final String TOPIC = "Topic";
	private static final String TIMEOUT = "TimeOut";

	public static String getTopic(Properties properties)
	{
		return properties.getProperty(TOPIC);
	}
	
	public static long getTimeout(Properties properties)
	{
		return Long.valueOf(properties.getProperty(TIMEOUT));
	}

}
