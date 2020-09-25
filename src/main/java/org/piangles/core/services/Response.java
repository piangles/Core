package org.piangles.core.services;

import java.io.Serializable;

public final class Response implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String serviceName;
	private String endPoint;
	private Object returnValue;

	public Response(String serviceName, String endPoint)
	{
		this(serviceName, endPoint, null);
	}
	
	public Response(String serviceName, String endPoint, Object returnValue)
	{
		this.serviceName = serviceName;
		this.endPoint = endPoint;
		this.returnValue = returnValue;
	}

	public String getServiceName()
	{
		return serviceName;
	}

	public String getEndPoint()
	{
		return endPoint;
	}

	public Object getReturnValue()
	{
		return returnValue;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("serviceName=").append(serviceName).append("\n");
		sb.append("endPoint=").append(endPoint).append("\n");
		sb.append("returnValue=").append("ReturnValue will not be disclosed.").append("\n");
		
		return sb.toString();
	}
}
