package org.piangles.core.services.remoting.controllers;

import java.util.Properties;

import org.piangles.core.services.Service;
import org.piangles.core.services.remoting.AbstractRemoter;
import org.piangles.core.services.remoting.SessionValidator;

public abstract class AbstractController extends AbstractRemoter implements Controller
{
	private static final String PRE_APPROVED_SESSION_ID = "PreApprovedSessionId";
	private static final String SESSION_VALIDATOR_CLASSNAME = "SessionValidatorClassName";
	
	private String preApprovedSessionId = null;
	private Service service = null;
	private SessionValidator sessionValidator = null;
	private boolean stopRequested = false;

	@Override
	public final void init(String serviceName, Properties properties) throws ControllerException
	{
		try
		{
			super.init(serviceName, properties);
			this.preApprovedSessionId = properties.getProperty(PRE_APPROVED_SESSION_ID);

			String sessionValidatorClassName = properties.getProperty(SESSION_VALIDATOR_CLASSNAME );
			
			sessionValidator = (SessionValidator)Class.forName(sessionValidatorClassName).newInstance();
		}
		catch (Exception e)
		{
			throw new ControllerException(e);
		}
		init();
	}
	
	@Override
	public final void start(Service service) throws ControllerException
	{
		this.service = service;  
		start();
	}

	@Override
	public final void stop() throws ControllerException
	{
		stopRequested = true;
	}

	protected final String getPreApprovedSessionId()
	{
		return preApprovedSessionId;
	}
	
	protected final Service getService()
	{
		return service;
	}
	
	protected boolean isStopRequested()
	{
		return stopRequested;
	}
	
	protected final SessionValidator getSessionValidator()
	{
		return sessionValidator;
	}
	
	protected abstract void init() throws ControllerException;
	protected abstract void start() throws ControllerException;
}
