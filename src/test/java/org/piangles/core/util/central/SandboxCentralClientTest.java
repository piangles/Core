package org.piangles.core.util.central;

public class SandboxCentralClientTest
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("FeaturesTestService Discovery:\n" + CentralClient.getInstance().discover("FeaturesTestService"));
		
		System.out.println("FeaturesTestService2 Discovery:\n" + CentralClient.getInstance().discover("FeaturesTestService2"));
		
		System.out.println("LoggingService Tier1Config:\n" + CentralClient.getInstance().tier1Config("LoggingService"));
		
		System.out.println("LoggingService Tier1Config:\n" + CentralClient.getInstance().tier1Config("LoggingService2"));
	}
}
