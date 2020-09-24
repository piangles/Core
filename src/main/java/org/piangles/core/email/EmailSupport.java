package org.piangles.core.email;

/**
 * This class is meant to connect to email server asynchronously and send a email.
 *
 */
public final class EmailSupport
{
	public static final void notify(Exception expt, String mesg)
	{
		System.out.println(expt);
		expt.printStackTrace();
	}
}
