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
