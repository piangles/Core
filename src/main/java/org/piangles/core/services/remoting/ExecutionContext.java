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
 
 
 
package org.piangles.core.services.remoting;

import org.piangles.core.stream.Stream;

public final class ExecutionContext
{
	private Stream<?> stream = null;
	
	private ExecutionContext(Stream<?> stream)
	{
		this.stream = stream;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Stream<T> getStream()
	{
		return (Stream<T>)stream;
	}
	
	public static final ExecutionContext get()
	{
		Stream<?> stream = null;
		
		Object currentThread = Thread.currentThread();
		if (currentThread instanceof StreamRequestProcessingThread)
		{
			stream = ((StreamRequestProcessingThread)currentThread).getStream();
		}
		
		return new ExecutionContext(stream);
	}
}
