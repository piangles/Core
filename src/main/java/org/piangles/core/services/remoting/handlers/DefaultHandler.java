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
 
 
 
package org.piangles.core.services.remoting.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.piangles.core.services.Request;
import org.piangles.core.services.Response;
import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamDetails;

public final class DefaultHandler extends AbstractHandler
{
	private Object service = null;

	public DefaultHandler(Object service)
	{
		this.service = service;
	}

	@Override
	protected void init()
	{
		
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		Object result = null;
		try
		{
			result = method.invoke(service, args);
		}
		catch (IllegalAccessException e)
		{
			result = e;
		}
		catch (IllegalArgumentException e)
		{
			result = e;
		}
		catch (InvocationTargetException e)
		{
			result = e;
		}
		return result;
	}

	@Override
	protected Response processRequest(Request request) throws Throwable
	{
		return null;
	}

	@Override
	protected Stream<?> createStream(StreamDetails details) throws Exception
	{
		//TODO Return InMemory Stream
		return null;
	}
}
