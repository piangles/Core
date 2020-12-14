package org.piangles.core.services.remoting;

import java.lang.reflect.Method;

import org.piangles.core.services.AuditDetails;
import org.piangles.core.services.Request;

public class AuditableControllerServiceDelegate extends AbstractService
{
	public AuditableControllerServiceDelegate(Object serviceImpl)
	{
		super(serviceImpl);
	}

	@Override
	protected Object process(Method method, Object[] args, Request request) throws Exception
	{
		AuditDetails auditDetails = new AuditDetails(request.getHeader(), request.getSourceInfo()); 
		Object[] modifiedArgs = new Object[1 + args.length];
		modifiedArgs[0] = auditDetails;
		System.arraycopy(args, 0, modifiedArgs, 1, args.length);
		
		return method.invoke(getServiceImpl(), modifiedArgs);
	}
}
