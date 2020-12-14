package org.piangles.core.stream;

public final class EndOfStream<T> implements Stream 
{
	@Override
	public void setMetadata(StreamMetadata metadata){}
	@Override
	public void add(Object streamlet){}
	@Override
	public void done(){}
	@Override
	public StreamMetadata getMetadata(){return null;}
	@SuppressWarnings("hiding")
	@Override
	public <T> void process(Processor<T> processor){}
	@SuppressWarnings("hiding")
	@Override
	public <T> void processAsync(Processor<T> processor){}
}
