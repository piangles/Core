package org.piangles.core.util.coding;

public class XML
{
	public static Encoder getEncoder()
	{
		return new XMLEncoderImpl();
	}
	
	public static Decoder getDecoder()
	{
		return new XMLDecoderImpl();
	}
}
