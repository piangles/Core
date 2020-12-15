package org.piangles.core.util.coding;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;

public class JavaDecoder implements Decoder
{
	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(byte[] data, Class<T> destClass) throws Exception
	{
		T returnValue = null;
		
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			ObjectInputStream in = new ObjectInputStream(bis);
			returnValue = (T)in.readObject();
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
		return decode(data, (Class<T>)null);
	}
}
