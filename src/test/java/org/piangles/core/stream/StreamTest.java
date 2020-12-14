package org.piangles.core.stream;

public class StreamTest
{
//	public StreamTest()
//	{
//		Stream<Integer> s = new EndOfStream<>();
//	}
	
	public Stream<Integer> getIntegerStream()
	{
		Stream<Integer> s = new EndOfStream<>();
		return s;
	}
}
