package com.TBD.core.util.coding;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;

public class JavaDecoder implements Decoder
{

	@Override
	public <T> T decode(byte[] data, Class<?> destClass) throws Exception
	{
		Object returnValue = null;
		
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			ObjectInputStream in = new ObjectInputStream(bis);
			returnValue = in.readObject();
		}
		catch (ClassNotFoundException | IOException e)
		{
			throw new Exception(e);
		}
		
		return (T)returnValue;
	}

	@Override
	public <T> T decode(byte[] data, Type destType) throws Exception
	{
		return decode(data, (Class)null);
	}

}
