package com.TBD.core.services.remoting.handlers;

import java.lang.reflect.Method;
import java.util.UUID;

import com.TBD.core.services.Context;
import com.TBD.core.services.Header;
import com.TBD.core.services.Request;
import com.TBD.core.services.SourceInfo;
import com.TBD.core.util.ClassHelper;

public class AuditableRequestCreator implements RequestCreator 
{
	@Override
	public Request createRequest(String userId, String sessionId, UUID traceId, String serviceName, Header header, Method method, Object[] args) throws Throwable
	{
		SourceInfo sourceInfo = null;
		ClassHelper classHelper = new ClassHelper(4);
		Object potentialContext = args[0];
		if (potentialContext instanceof Context)
		{
			Context context = (Context) potentialContext;
			header = context.getHeader();
			sourceInfo = context.getSourceInfo();
			Object[] newArgs = new Object[args.length-1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
			args = newArgs;
		}
		else
		{
			sourceInfo = new SourceInfo(classHelper.getClassName(), classHelper.getLineNumber(), 
										classHelper.getCompleteStackTrace(), null);
		}
		return new Request(userId, sessionId, traceId, header, sourceInfo, serviceName, method.getName(), args);
	}
}
