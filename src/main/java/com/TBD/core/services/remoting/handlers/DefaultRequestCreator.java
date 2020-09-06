package com.TBD.core.services.remoting.handlers;

import java.lang.reflect.Method;
import java.util.UUID;

import com.TBD.core.services.Header;
import com.TBD.core.services.Request;

public class DefaultRequestCreator implements RequestCreator 
{
	@Override
	public Request createRequest(String userId, String sessionId, UUID traceId, String serviceName, Header header, Method method, Object[] args) throws Throwable
	{
		return new Request(userId, sessionId, traceId, header, serviceName, method.getName(), args);
	}
}
