package org.piangles.core.services;

import java.io.Serializable;

public final class SourceInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String className = null;
	private String lineNumber = null;
	private String stackTrace = null;
	private String authorizationId = null;
	
	public SourceInfo(String className, String lineNumber, String stackTrace, String authorizationId)
	{
		super();
		this.className = className;
		this.lineNumber = lineNumber;
		this.stackTrace = stackTrace;
		this.authorizationId = authorizationId;
	}
	
	public String getClassName()
	{
		return className;
	}
	
	public String getLineNumber()
	{
		return lineNumber;
	}
	
	public String getStackTrace()
	{
		return stackTrace;
	}

	public String getAuthorizationId()
	{
		return authorizationId;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("AuthorizationId=").append(authorizationId).append("\n");
		sb.append("className=").append(className).append("\n");
		sb.append("lineNumber=").append(lineNumber).append("\n");
		sb.append("stackTrace=").append("\n").append(stackTrace).append("\n");
		
		return sb.toString();
	}
}
