package com.TBD.core.util;

import com.TBD.core.util.coding.XML;

public class XMLEncoderTest
{
	public static void main(String[] args)
	{
		try
		{
			String object = "Message";
			byte[] data = XML.getEncoder().encode(object);
			System.out.println(new String(data));
			System.out.println("Deserialized : " + XML.getDecoder().decode(data, null));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
