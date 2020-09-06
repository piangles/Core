package com.TBD.core.util.coding;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;

public class XMLEncoderImpl implements Encoder
{
	@Override
	public byte[] encode(Object object) throws Exception
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			XMLEncoder xmlEncoder = new XMLEncoder(bos);
			xmlEncoder.writeObject(object);
			xmlEncoder.close();
		}
		catch (RuntimeException e)
		{
			throw new  Exception(e);
		}
		return bos.toByteArray();
		
	}
}
