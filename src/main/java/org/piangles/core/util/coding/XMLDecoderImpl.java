/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.core.util.coding;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;

public class XMLDecoderImpl implements Decoder
{
	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(byte[] data, Class<T> destClass) throws Exception
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
