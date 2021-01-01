package org.piangles.core.util.central;

import java.util.Properties;

public abstract class CentralClient
{
	public static final String CENTRAL_CLIENT_CLASS = "central.client.class";
	private static CentralClient centralClient = null; 
	
	public static CentralClient getInstance()
	{
		if (centralClient == null)
		{
			synchronized (CentralClient.class)
			{
				if (centralClient == null)
				{
					try
					{
						String centralClientClassName = System.getenv(CENTRAL_CLIENT_CLASS);
						if (centralClientClassName == null)
						{
							centralClientClassName = DefaultCentralClient.class.getCanonicalName();
							System.err.println(CENTRAL_CLIENT_CLASS + " property is NOT set, defaulting to : " + centralClientClassName);
						}
						else
						{
							System.out.println(CENTRAL_CLIENT_CLASS + " property is set, trying to create : " + centralClientClassName);
						}
						centralClient = (CentralClient)Class.forName(centralClientClassName).newInstance();
					}
					catch(Throwable t)
					{
						throw new Error(t);
					}
				}
			}
		}
		return centralClient;
	}
	
	public abstract Properties discover(String serviceName) throws Exception;
	public abstract Properties tier1Config(String serviceName) throws Exception;
	public abstract String decrypt(String serviceName, String encryptedCategory, String encryptedValueName, String encryptedValue, 
			String cipherAuthorizationIdName, String cipherAuthorizationId)throws Exception;
}
