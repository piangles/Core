package org.piangles.core.services;

import java.util.UUID;

public final class Context 
{
	private UUID traceId = null;
	private Header header = null;
	private SourceInfo sourceInfo = null;
	
	public Context(UUID traceId, Header header, SourceInfo sourceInfo)
	{
		this.traceId = traceId;
		this.header = header;
		this.sourceInfo = sourceInfo;
	}
	
	public UUID getTraceId()
	{
		return traceId;
	}
	
	public Header getHeader()
	{
		return header;
	}

	public SourceInfo getSourceInfo()
	{
		return sourceInfo;
	}

	@Override
	public String toString()
	{
		return "Context [traceId=" + traceId + ", header=" + header + ", sourceInfo=" + sourceInfo + "]";
	}
}
