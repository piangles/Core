package org.piangles.core.util.coding;

import java.lang.reflect.Type;

import com.google.gson.Gson;

final class JSONDecoder implements Decoder
{
	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(byte[] data, Class<?> destClass) throws Exception
	{
		T decodeObject = null;
		try
		{
			decodeObject = (T) new Gson().fromJson(new String(data), destClass); 
		}
		catch (RuntimeException expt)
		{
			throw new Exception(expt);
		}
		return decodeObject;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(byte[] data, Type destType) throws Exception
	{
		T decodeObject = null;
		try
		{
			decodeObject = (T) new Gson().fromJson(new String(data), destType);; 
		}
		catch (RuntimeException expt)
		{
			throw new Exception(expt);
		}
		return decodeObject;
	}
}
