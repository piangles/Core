package org.piangles.core.stream;

import java.io.Serializable;

public final class StreamDetails implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String queueName = null;
	
	public StreamDetails(String queueName)
	{
		this.queueName = queueName;
	}

	public String getQueueName()
	{
		return queueName;
	}
}
