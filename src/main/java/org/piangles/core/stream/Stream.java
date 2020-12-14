package org.piangles.core.stream;

public interface Stream
{
	//Producer methods
	public void setMetadata(StreamMetadata metadata);
	public void add(Object obj);
	public void done();

	//Consumer methods
	public StreamMetadata getMetadata() throws Exception;
	public <T> void process(Processor<T> processor) throws Exception;
	public <T> void processAsync(Processor<T> processor) throws Exception;
}
