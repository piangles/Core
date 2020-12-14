package org.piangles.core.stream;

import org.piangles.core.services.remoting.ExecutionContext;

public class StreamTest
{
	public StreamTest()
	{
	}
	
	public Stream<Integer> getIntegerStream()
	{
		Stream<Integer> s = new EndOfStream<>();
		return s;
	}
	
	public Stream<String> getStringStream()
	{
		Stream<String> s = ExecutionContext.get().getStream();
		return s;
	}
}
