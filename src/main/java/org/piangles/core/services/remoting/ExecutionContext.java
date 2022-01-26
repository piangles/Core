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

import java.util.UUID;

import org.piangles.core.stream.Stream;

public final class ExecutionContext
{
	private UUID traceId = null;
	private SessionDetails sessionDetails = null;
	private Stream<?> stream = null;
	
	private ExecutionContext(UUID traceId, SessionDetails sessionDetails, Stream<?> stream)
	{
		this.traceId = traceId;
		this.sessionDetails = sessionDetails;
		this.stream = stream;
	}
	
	public static final ExecutionContext get()
	{
		UUID traceId = null;
		SessionDetails sessionDetails = null;
		Stream<?> stream = null;
		
		Object currentThread = Thread.currentThread();
		if (currentThread instanceof StreamRequestProcessingThread)
		{
			stream = ((StreamRequestProcessingThread)currentThread).getStream();
		}
		
		if (currentThread instanceof AbstractContextAwareThread)
		{
			traceId = ((AbstractContextAwareThread)currentThread).getTraceId();
			sessionDetails = ((AbstractContextAwareThread)currentThread).getSessionDetails(); 
		}
		
		if (traceId == null && currentThread instanceof Traceable)
		{
			traceId = ((Traceable)currentThread).getTraceId();
		}

		if (sessionDetails == null && currentThread instanceof SessionAwareable)
		{
			sessionDetails = ((SessionAwareable)currentThread).getSessionDetails();
		}

		return new ExecutionContext(traceId, sessionDetails, stream);
	}

	public UUID getTraceId()
	{
		return traceId;
	}

	public SessionDetails getSessionDetails()
	{
		return sessionDetails;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Stream<T> getStream()
	{
		return (Stream<T>)stream;
	}
}
