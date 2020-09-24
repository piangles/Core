package org.piangles.core.util.coding;

import java.lang.reflect.Type;

public interface Decoder
{
	public <T> T decode(byte[] data, Class<?> destClass) throws Exception;
	public <T> T decode(byte[] data, Type destType) throws Exception;
}
