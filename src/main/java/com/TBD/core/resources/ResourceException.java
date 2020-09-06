package com.TBD.core.resources;

public class ResourceException extends Exception
{
	public ResourceException(String message)
	{
		super(message);
	}

	public ResourceException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ResourceException(Throwable cause)
	{
		super(cause);
	}
}
