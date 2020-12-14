package org.piangles.core.services.remoting;

import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamDetails;

public interface ResponseSender
{
	public Stream<?> createStream(StreamDetails streamDetails) throws Exception;
	public void send(byte[] encodedBytes) throws Exception;
}
