package org.piangles.core.util.coding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class JavaEncoder implements Encoder
{

	@Override
	public byte[] encode(Object object) throws Exception
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(object);
		}
		catch (IOException e)
		{
			throw new  Exception(e);
		}
		return bos.toByteArray();
	}

}
