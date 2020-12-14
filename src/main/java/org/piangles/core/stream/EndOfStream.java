package org.piangles.core.stream;

public final class EndOfStream<T> implements Stream<T> 
{
	@Override
	public void setMetadata(StreamMetadata metadata){}
	@Override
	public void add(Object streamlet){}
	@Override
	public void done(){}
	@Override
	public StreamMetadata getMetadata(){return null;}
	@Override
	public void process(StreamProcessor<T> processor){}
	@Override
	public void processAsync(StreamProcessor<T> processor){}
}
