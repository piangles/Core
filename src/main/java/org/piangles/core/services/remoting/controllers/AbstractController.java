/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
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
