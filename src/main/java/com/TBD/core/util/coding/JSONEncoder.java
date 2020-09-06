package com.TBD.core.util.coding;

import com.google.gson.Gson;

class JSONEncoder implements Encoder
{
	public byte[] encode(Object object) throws Exception	
	{
		byte[] jsonMessage = null;
		try
		{
			jsonMessage = new Gson().toJson(object).getBytes();
		}
		catch(RuntimeException e)
		{
			throw new Exception(e);
		}
		return jsonMessage; 
	}
}
