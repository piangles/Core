package org.piangles.core.util.central;

public class CentralClientTest
{
	public static void main(String[] args) throws Exception
	{
		System.out.println(CentralClient.getInstance().discover("SessionManagementService"));
	}
}
