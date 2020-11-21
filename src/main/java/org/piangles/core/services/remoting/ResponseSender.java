package org.piangles.core.services.remoting;

public interface ResponseSender
{
	public void send(byte[] encodedBytes) throws Exception;
}
