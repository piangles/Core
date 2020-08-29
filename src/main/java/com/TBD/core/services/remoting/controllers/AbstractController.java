package com.TBD.core.services.remoting.controllers;

import java.util.Properties;

import com.TBD.core.services.Service;
import com.TBD.core.services.remoting.SessionValidator;

public abstract class AbstractController implements Controller
{
	private static final String SESSION_VALIDATOR_CLASSNAME = "SessionValidatorClassName";
	
	private boolean stopRequested = false;
	private Service service = null;
	private SessionValidator sessionValidator = null;

	@Override
	public void init(Properties properties) throws ControllerException
	{
		try
		{
			String sessionValidatorClassName = properties.getProperty(SESSION_VALIDATOR_CLASSNAME );
			
			sessionValidator = (SessionValidator)Class.forName(sessionValidatorClassName).newInstance();
		}
		catch (Exception e)
		{
			throw new ControllerException(e);
		}
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

	protected final Service getService()
	{
		return service;
	}
	
	protected final boolean isStopRequested()
	{
		return stopRequested;
	}
	
	protected final SessionValidator getSessionValidator()
	{
		return sessionValidator;
	}
	
	protected abstract void start() throws ControllerException;
}
