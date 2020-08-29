package com.TBD.core.services.remoting.rest;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.core.util.MultivaluedMapImpl;

final class Request
{
	private String endPointName = null;
	private Actions action = null;
	private MultivaluedMap<String, String> params = null;
	private Object body = null;
	
	public Request(String endPointName, Actions action)
	{
		this.endPointName = endPointName;
		this.action = action;
		this.params = new MultivaluedMapImpl();
	}

	public String getEndPointName()
	{
		return endPointName;
	}

	public Actions getAction()
	{
		return action;
	}

	public MultivaluedMap<String, String> getParams()
	{
		return params;
	}

	public Object getBody()
	{
		return body;
	}
	
	public void setBody(Object body)
	{
		this.body = body;
	}

	@Override
	public String toString()
	{
		return "Request [endPointName=" + endPointName + ", action=" + action + ", params=" + params + ", body=" + body + "]";
	}
}
