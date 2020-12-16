package org.piangles.core.stream;

public final class StartOfStream<T> implements Stream<T> 
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
