package org.piangles.core.stream;

public interface StreamProcessor<T,U>
{
	public U process(T obj);
}
