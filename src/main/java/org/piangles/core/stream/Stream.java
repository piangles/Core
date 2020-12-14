package org.piangles.core.stream;

public interface Stream<T>
{
	//Producer methods
	public void setMetadata(StreamMetadata metadata);
	public void add(T streamlet);
	public void done();

	//Consumer methods
	public StreamMetadata getMetadata() throws Exception;
	public void process(Processor<T> processor) throws Exception;
	public void processAsync(Processor<T> processor) throws Exception;
}
