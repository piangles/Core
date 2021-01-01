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

import org.piangles.core.services.Request;
import org.piangles.core.services.Service;
import org.piangles.core.stream.Stream;

/**
 * On the server side each streaming request needs to be processed in a separate thread.
 * 
 * Dolittle (2020) 
 * 
 * John Dolittle: No, no, Cheech. It's okay to be scared.
 */
final class StreamRequestProcessingThread extends AbstractContextAwareThread
{
	private Service service = null;
	private Request request = null;
	private Stream<?> stream;

	StreamRequestProcessingThread(Service service, Request request, Stream<?> stream)
	{
		this.service = service;
		this.request = request;
		this.stream = stream;
	}

	public void run()
	{
		//This will return a Response object encapsulating an EndOfStream object : We will ignore it.
		service.process(request);
		
		//Notify EndOfStream to consumer and close it.
		stream.done();
	}
	
	Stream<?> getStream()
	{
		return stream;
	}
}
