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
 
 
 
package org.piangles.core.stream;

import java.io.Serializable;

public final class Streamlet<T> implements Serializable
{
	private static final String EOS = "EndOfStream";

	private static final long serialVersionUID = 1L;
	
	private String type = null;
	private StreamMetadata metadata = null;
	private T payload = null;

	public Streamlet()
	{
		type = EOS;
	}

	public Streamlet(StreamMetadata metadata)
	{
		type = metadata.getClass().getCanonicalName();
		this.metadata = metadata;
	}

	public Streamlet(T payload)
	{
		type = payload.getClass().getCanonicalName();
		this.payload = payload;
	}

	public String getType()
	{
		return type;
	}
	
	public StreamMetadata getMetadata()
	{
		return metadata;
	}

	public T getPayload()
	{
		return payload;
	}
	
	public boolean isEndOfStreamMessage()
	{
		return EOS.equals(type);
	}

	@Override
	public String toString()
	{
		return "Streamlet [type=" + type + ", metadata=" + metadata + ", payload=" + payload + "]";
	}
}
