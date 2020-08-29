package com.TBD.core.services.remoting;

import com.TBD.core.services.Request;

public interface SessionValidator
{
	public boolean isSessionValid(Request request) throws Exception;
}
