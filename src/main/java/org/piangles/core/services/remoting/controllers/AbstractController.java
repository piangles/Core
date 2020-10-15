package org.piangles.core.services.remoting.controllers;

import java.util.Properties;

import org.piangles.core.services.Service;
import org.piangles.core.services.remoting.SessionValidator;

public abstract class AbstractController implements Controller
{
	private static final String SESSION_VALIDATOR_CLASSNAME = "SessionValidatorClassName";
	
	private String serviceName = null;
	private Service service = null;
	private SessionValidator sessionValidator = null;
	private boolean stopRequested = false;

	@Override
	public final void init(String serviceName, Properties properties) throws ControllerException
	{
		try
		{
			this.serviceName = serviceName; 
			String sessionValidatorClassName = properties.getProperty(SESSION_VALIDATOR_CLASSNAME );
			
			sessionValidator = (SessionValidator)Class.forName(sessionValidatorClassName).newInstance();
		}
		catch (Exception e)
		{
			throw new ControllerException(e);
		}
		init(properties);
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

	protected final String getServiceName()
	{
		return serviceName;
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
	
	protected abstract void init(Properties properties) throws ControllerException;
	protected abstract void start() throws ControllerException;
}
