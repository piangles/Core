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

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Date;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

final class JSONDecoder implements Decoder
{
	private GsonBuilder gsonBuilder = null;

	public JSONDecoder()
	{
		gsonBuilder = new GsonBuilder();

		gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>()
		{
			@Override
			public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				return new Date(json.getAsJsonPrimitive().getAsLong());
			}
		});
		
		gsonBuilder.registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>()
		{
			@Override
			public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
			}
		});
	}

	@Override
	public <T> T decode(byte[] data, Class<T> destClass) throws Exception
	{
		T decodeObject = null;
		try
		{
			decodeObject = (T) gsonBuilder.create().fromJson(new String(data), destClass);
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
			decodeObject = (T) gsonBuilder.create().fromJson(new String(data), destType);
		}
		catch (RuntimeException expt)
		{
			throw new Exception(expt);
		}
		return decodeObject;
	}
}
