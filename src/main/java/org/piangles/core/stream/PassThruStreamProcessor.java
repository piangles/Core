package org.piangles.core.stream;

public final class PassThruStreamProcessor<I,O> implements StreamProcessor<I,O>
{
	@SuppressWarnings("unchecked")
	public O process(I obj)
	{
		return (O)obj;
	}
}
