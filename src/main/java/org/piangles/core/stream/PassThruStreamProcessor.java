package org.piangles.core.stream;

import java.util.Optional;

public final class PassThruStreamProcessor<I,O> implements StreamProcessor<I,O>
{
	@SuppressWarnings("unchecked")
	public Optional<O> process(Optional<I> obj)
	{
		return (Optional<O>)obj;
	}
}
