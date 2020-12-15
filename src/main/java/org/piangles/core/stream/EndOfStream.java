package org.piangles.core.stream;

public final class EndOfStream<T> implements Stream<T> 
{
	@Override
	public void setMetadata(StreamMetadata metadata){}
	@Override
	public void add(T streamlet){}
	@Override
	public void done(){}
	@Override
	public StreamMetadata getMetadata(){return null;}
	@Override
	public <U> void processAsync(StreamProcessor<T,U> processor){}
}
