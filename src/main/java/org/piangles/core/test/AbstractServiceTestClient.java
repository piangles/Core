package org.piangles.core.test;

import org.piangles.core.services.remoting.SessionAwareable;
import org.piangles.core.services.remoting.SessionDetails;

public abstract class AbstractServiceTestClient extends Thread implements SessionAwareable
{
	public static String cipherAuthorizationId = "7a948dce-1ebb-4770-b077-f453e60243da";

	public final void run()
	{
		try
		{
			runImpl();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.exit(1);
	}
	
	@Override
	public final SessionDetails getSessionDetails()
	{
		return new SessionDetails("FeaturesTestService", "TODOSessionId");
	}
	
	public abstract void runImpl() throws Exception;
}
