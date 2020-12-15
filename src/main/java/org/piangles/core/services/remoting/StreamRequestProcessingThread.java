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
