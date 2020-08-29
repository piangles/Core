package com.TBD.core.services.remoting.jms;

import java.util.Properties;

public class JMSProperties
{
	private String connectionFactoryName;
	private String userName;
	private String password;
	private boolean secure;
	private boolean topic;
	private String publishTopic;
	private String subscribeTopic;
	private String writeQ;
	private String readQ;
	
	public JMSProperties(Properties properties)
	{
		//Retrieve from properties and put it in variables 
	}

	public String getConnectionFactoryName()
	{
		return connectionFactoryName;
	}

	public String getUserName()
	{
		return userName;
	}

	public String getPassword()
	{
		return password;
	}

	public boolean isSecure()
	{
		return secure;
	}

	public boolean isTopic()
	{
		return topic;
	}

	public String getPublishTopic()
	{
		return publishTopic;
	}

	public String getSubscribeTopic()
	{
		return subscribeTopic;
	}

	public String getWriteQ()
	{
		return writeQ;
	}

	public String getReadQ()
	{
		return readQ;
	}
	
	
}
