package org.piangles.core.stream;

import java.io.Serializable;

public final class StreamMetadata implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int streamletCount = -1; //It is not determinable
	
	public StreamMetadata()
	{
		
	}
	
	public StreamMetadata(int streamletCount)
	{
		this.streamletCount = streamletCount;
	}

	public int getStreamletCount()
	{
		return streamletCount;
	}
}
