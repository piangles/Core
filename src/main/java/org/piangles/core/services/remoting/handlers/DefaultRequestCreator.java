package org.piangles.core.services.remoting.handlers;

import java.lang.reflect.Method;
import java.util.UUID;

import org.piangles.core.services.Header;
import org.piangles.core.services.Request;

public class DefaultRequestCreator implements RequestCreator 
{
	@Override
	public Request createRequest(String userId, String sessionId, UUID traceId, String serviceName, Header header, Method method, Object[] args) throws Throwable
	{
		return new Request(userId, sessionId, traceId, header, serviceName, method.getName(), args);
	}
}
