package com.TBD.core.util;

import com.TBD.core.util.central.CentralClient;

public class CentralClientTest
{
	public static void main(String[] args) throws Exception
	{
		System.out.println(CentralClient.tier1Config("ConfigService"));
		System.out.println(CentralClient.discover("ConfigService"));
		System.out.println(CentralClient.decrypt(
				"SessionManagementService",
				"Discovery", 
				"RMQLoginId", "TdmVTwllpL3hE+HyDz1ScA==", //Call Param
				"ControllerDecrypterAuthorizationId", "477e0c1b-d057-40df-9c56-e7c52ddb875d" //Decrypter Constructor params
				));
	}
}
