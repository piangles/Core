package org.piangles.core.services.remoting;

import org.piangles.core.services.Request;

public interface SessionValidator
{
	public boolean isSessionValid(Request request) throws Exception;
}
