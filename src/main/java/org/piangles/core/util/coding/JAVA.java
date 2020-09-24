package org.piangles.core.util.coding;

public class JAVA
{
	public static Encoder getEncoder()
	{
		return new JavaEncoder();
	}
	
	public static Decoder getDecoder()
	{
		return new JavaDecoder();
	}
}
