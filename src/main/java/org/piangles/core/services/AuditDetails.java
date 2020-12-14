package org.piangles.core.services;

public final class AuditDetails
{
	private Header header = null;
	private SourceInfo sourceInfo = null;
	
	public AuditDetails(Header header, SourceInfo sourceInfo)
	{
		this.header = header;
		this.sourceInfo = sourceInfo;
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
		return "Context [header=" + header + ", sourceInfo=" + sourceInfo + "]";
	}
}
