package org.piangles.core.stream;

public interface StreamProcessor<I,O>
{
	public O process(I obj);
}
