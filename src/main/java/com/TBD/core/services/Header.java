package com.TBD.core.services;

import java.io.Serializable;

public final class Header implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private long createdTimeInMillis = 0L;
	private String hostName = null;
	private String loginId = null;
	private String processName = null;
	private String processId = null;
	private String threadId = null;
	
	public Header(String hostName, String loginId, String processName, String processId, String threadId)
	{
		this.createdTimeInMillis = System.currentTimeMillis(); 
		this.hostName = hostName;
		this.loginId = loginId;
		this.processName = processName;
		this.processId = processId;
		this.threadId = threadId;
	}

	public long getCreatedTimeInMillis()
	{
		return createdTimeInMillis;
	}
	
	public String getHostName()
	{
		return hostName;
	}

	public String getLoginId()
	{
		return loginId;
	}

	public String getProcessName()
	{
		return processName;
	}

	public String getProcessId()
	{
		return processId;
	}

	public String getThreadId()
	{
		return threadId;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("createdTimeInMillis=").append(createdTimeInMillis).append("\n");
		sb.append("hostName=").append(hostName).append("\n");
		sb.append("loginId=").append(loginId).append("\n");
		sb.append("processName=").append(processName).append("\n");
		sb.append("processId=").append(processId).append("\n");
		sb.append("threadId=").append(threadId).append("\n");
		return sb.toString();
	}
}
