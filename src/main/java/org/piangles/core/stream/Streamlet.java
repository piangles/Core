package org.piangles.core.stream;

import java.io.Serializable;

public final class Streamlet<T> implements Serializable
{
	private static final String EOS = EndOfStream.class.getSimpleName();

	private static final long serialVersionUID = 1L;
	
	private String type = null;
	private StreamMetadata metadata = null;
	private T payload = null;

	public Streamlet()
	{
		type = EOS;
	}

	public Streamlet(StreamMetadata metadata)
	{
		type = metadata.getClass().getCanonicalName();
		this.metadata = metadata;
	}

	public Streamlet(T payload)
	{
		type = payload.getClass().getCanonicalName();
		this.payload = payload;
	}

	public String getType()
	{
		return type;
	}
	
	public StreamMetadata getMetadata()
	{
		return metadata;
	}

	public T getPayload()
	{
		return payload;
	}
	
	public boolean isEndOfStreamMessage()
	{
		return EOS.equals(type);
	}

	@Override
	public String toString()
	{
		return "Streamlet [type=" + type + ", metadata=" + metadata + ", payload=" + payload + "]";
	}
}
