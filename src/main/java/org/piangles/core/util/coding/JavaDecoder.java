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
