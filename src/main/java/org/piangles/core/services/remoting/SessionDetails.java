package org.piangles.core.services.remoting;

public final class SessionDetails
{
	private String userId = null;
	private String sessionId = null;
	
	public SessionDetails(String userId, String sessionId)
	{
		this.userId = userId;
		this.sessionId = sessionId;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	@Override
	public String toString()
	{
		return "SessionDetails [userId=" + userId + ", sessionId=" + sessionId + "]";
	}
}
