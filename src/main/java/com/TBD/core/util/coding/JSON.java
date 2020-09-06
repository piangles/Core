package com.TBD.core.util.coding;

public class JSON
{
	public static Encoder getEncoder()
	{
		return new JSONEncoder();
	}
	
	public static Decoder getDecoder()
	{
		return new JSONDecoder();
	}
}
