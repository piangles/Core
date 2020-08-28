package com.TBD.core.services;

import java.io.Serializable;
import java.util.UUID;

public final class Request implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String userId = null;
	private String sessionId = null;	
	private UUID traceId = null;
	private Header header = null;
	private SourceInfo sourceInfo = null;
	
	private String serviceName;
	private String endPoint;
	private Object[] parameters;

	public Request(String userId, String sessionId, UUID traceId, Header header, String serviceName, String endPoint)
	{
		this(userId, sessionId, traceId, header, serviceName, endPoint, null);
	}

	public Request(String userId, String sessionId, UUID traceId, Header header, String serviceName, String endPoint, Object[] parameters)
	{
		this(userId, sessionId, traceId, header, null, serviceName, endPoint, parameters);
	}

	public Request(String userId, String sessionId, UUID traceId, Header header, SourceInfo sourceInfo, String serviceName, String endPoint, Object[] parameters)
	{
		this.userId = userId;
		this.sessionId = sessionId;
		this.traceId = traceId;
		
		this.header = header;
		this.sourceInfo = sourceInfo;
		
		this.serviceName = serviceName;
		this.endPoint = endPoint;
		this.parameters = parameters;
	}

	public String getUserId()
	{
		return userId;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public UUID getTraceId()
	{
		return traceId;
	}
	
	public Header getHeader()
	{
		return header;
	}

	public String getServiceName()
	{
		return serviceName;
	}

	public String getEndPoint()
	{
		return endPoint;
	}

	public Object[] getParameters()
	{
		return parameters;
	}
	
	public SourceInfo getSourceInfo()
	{
		return sourceInfo;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("userId=").append(userId).append("\n");
		sb.append("sessionId=").append(sessionId).append("\n");
		sb.append("traceId=").append(traceId).append("\n");
		sb.append("header=").append(header).append("\n");
		sb.append("sourceInfo=").append(sourceInfo).append("\n");
		sb.append("serviceName=").append(serviceName).append("\n");
		sb.append("endPoint=").append(endPoint).append("\n");
		sb.append("parameters=").append("Parameter will not be disclosed.").append("\n");
		
		return sb.toString();
	}
}
