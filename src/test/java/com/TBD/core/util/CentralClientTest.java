package com.TBD.core.util;

import com.TBD.core.util.central.CentralClient;

public class CentralClientTest
{
	public static void main(String[] args) throws Exception
	{
		System.out.println(CentralClient.tier1Config("ConfigService"));
		System.out.println(CentralClient.discover("LoggingService"));
		System.out.println(CentralClient.decrypt("1c044b37-9388-4e6b-baf2-9fe09dea4281", "TdmVTwllpL3hE+HyDz1ScA=="));
	}
}
