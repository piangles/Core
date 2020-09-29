package org.piangles.core.services.remoting.handlers;

import java.lang.reflect.Method;
import java.util.UUID;

import org.piangles.core.services.Context;
import org.piangles.core.services.Header;
import org.piangles.core.services.Request;
import org.piangles.core.services.SourceInfo;
import org.piangles.core.util.ClassHelper;

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