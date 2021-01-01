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
 
 
 
package org.piangles.core.services.remoting;

import java.util.UUID;

public class SessionImpersonator<R, E extends Exception> extends Thread implements SessionAwareable, Traceable
{
	//TODO Rename this to SessionImpersonator
	private ServiceTask task = null;
	private R response = null;
	private Exception expt = null;
	private SessionDetails sessionDetails = null;
	private UUID traceId = null;
	
	public SessionImpersonator(ServiceTask task)
	{
		Thread currentThread = Thread.currentThread();
		if (currentThread instanceof RequestProcessingThread)
		{
			RequestProcessingThread rpt = (RequestProcessingThread) currentThread;
			this.traceId = rpt.getTraceId();
			this.sessionDetails = new SessionDetails(rpt.getServiceName(), rpt.getPreApprovedSessionId());
		}
		this.task = task;
	}
	
	public R execute() throws E
	{
		try
		{
			start();
			join();
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
		if (expt != null)
		{
			throw (E)expt;
		}
		return (R)response;
	}
	
	public void run()
	{
		try
		{
			response = (R)task.execute();
		}
		catch (Exception e)
		{
			expt = e;
		}
	}
	
	@Override
	public SessionDetails getSessionDetails()
	{
		return sessionDetails;
	}

	@Override
	public UUID getTraceId()
	{
		return traceId;
	}
}
