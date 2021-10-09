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
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

class JSONEncoder implements Encoder
{
	private GsonBuilder gsonBuilder = null;

	public JSONEncoder()
	{
		gsonBuilder = new GsonBuilder();

		gsonBuilder.registerTypeAdapter(Date.class, new JsonSerializer<Date>()
		{
			@Override
			public JsonElement serialize(Date src, Type typeOfT, JsonSerializationContext context) throws JsonParseException
			{
				return new JsonPrimitive(src.getTime());
			}
		});

		gsonBuilder.registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>()
		{
			@Override
			public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context)
			{
		        return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
			}
		});
	}

	public byte[] encode(Object object) throws Exception
	{
		byte[] jsonMessage = null;
		try
		{
			jsonMessage = gsonBuilder.create().toJson(object).getBytes();
		}
		catch (RuntimeException e)
		{
			throw new Exception(e);
		}
		return jsonMessage;
	}
}
