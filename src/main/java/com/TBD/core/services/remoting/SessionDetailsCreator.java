package com.TBD.core.services.remoting;

import java.util.Properties;

import com.TBD.core.util.central.CentralClient;

public final class SessionDetailsCreator
{
	private static final String USER_NAME = "user.name";
	private static final String PREDETERMINED_SESSIONID = "PredeterminedSessionId";
	
	
	public static SessionDetails createSessionDetails(String serviceName) throws Exception
	{
		return createSessionDetails(serviceName, CentralClient.discover(serviceName));
	}
	
	public static SessionDetails createSessionDetails(String serviceName, Properties props) throws Exception
	{
		return new SessionDetails(System.getProperty(USER_NAME), serviceName + "-" + props.getProperty(PREDETERMINED_SESSIONID));
	}
}
