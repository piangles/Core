package org.piangles.core.services.remoting;

import java.util.Properties;

import org.piangles.core.util.central.CentralClient;

public final class SessionDetailsCreator
{
	private static final String PREDETERMINED_SESSIONID = "PredeterminedSessionId";
	
	
	public static SessionDetails createSessionDetails(String serviceName) throws Exception
	{
		return createSessionDetails(serviceName, CentralClient.discover(serviceName));
	}
	
	static SessionDetails createSessionDetails(String serviceName, Properties props) throws Exception
	{
		return new SessionDetails(serviceName, props.getProperty(PREDETERMINED_SESSIONID));
	}
}
