package org.piangles.core.stream;

public interface Stream<I>
{
	//Producer methods
	public void setMetadata(StreamMetadata metadata);
	public void add(I obj);
	public void done();

	//Consumer methods
	public StreamMetadata getMetadata() throws Exception;
	public <O> void processAsync(StreamProcessor<I,O> processor) throws Exception;
}
