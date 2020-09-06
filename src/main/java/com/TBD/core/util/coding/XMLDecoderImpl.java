package com.TBD.core.util.coding;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;

public class XMLDecoderImpl implements Decoder
{
	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(byte[] data, Class<?> destClass) throws Exception
	{
		T decodeObject = null;
		XMLDecoder xmlDecoder = null;
		try
		{
			xmlDecoder = new XMLDecoder(new ByteArrayInputStream(data));
			decodeObject = (T)xmlDecoder.readObject(); 
		}
		catch (RuntimeException expt)
		{
			throw new Exception(expt);
		}
		finally
		{
			if (xmlDecoder != null)
			{
				xmlDecoder.close();
			}
		}
		return decodeObject;
	}

	@Override
	public <T> T decode(byte[] data, Type destType) throws Exception
	{
		return decode(data, null);
	}
}
