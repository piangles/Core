package org.piangles.core.stream;

import java.util.Optional;

public interface StreamProcessor<I,O>
{
	public Optional<O> process(Optional<I> obj);
}
