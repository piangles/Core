package org.piangles.core.stream;

public interface Stream<T>
{
	//Producer methods
	public void setMetadata(StreamMetadata metadata);
	public void add(T obj);
	public void done();

	//Consumer methods
	public StreamMetadata getMetadata() throws Exception;
	public void process(StreamProcessor<T> processor) throws Exception;
	public void processAsync(StreamProcessor<T> processor) throws Exception;
}
