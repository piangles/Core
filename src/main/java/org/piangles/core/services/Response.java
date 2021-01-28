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
 
 
 
package org.piangles.core.services;

import java.io.Serializable;
import java.util.Date;

public final class Response implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Date issuedTime = null;
	private long requestTransitTime = 0L;
	private long transitTime = 0L;
	
	private String serviceName;
	private String endPoint;
	private Object returnValue;

	public Response(String serviceName, String endPoint, Object returnValue)
	{
		this(serviceName, endPoint, 0L, returnValue);
	}
	
	public Response(String serviceName, String endPoint, long requestTransitTime, Object returnValue)
	{
		this.issuedTime = new Date();
		this.requestTransitTime = requestTransitTime;
		
		this.serviceName = serviceName;
		this.endPoint = endPoint;
		this.returnValue = returnValue;
	}

	public String getServiceName()
	{
		return serviceName;
	}

	public String getEndPoint()
	{
		return endPoint;
	}

	public Date getIssuedTime()
	{
		return issuedTime;
	}

	public long getRequestTransitTime()
	{
		return requestTransitTime;
	}

	public void markTransitTime()
	{
		transitTime = System.currentTimeMillis() - issuedTime.getTime(); 
	}
	
	public long getTransitTime()
	{
		return transitTime;
	}

	public Object getReturnValue()
	{
		return returnValue;
	}
	
	public boolean isSuccessful()
	{
		return !(returnValue instanceof Throwable);
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("ResponseIssuedTime=").append(issuedTime).append("\n");
		sb.append("requestTransitTime=").append(requestTransitTime).append("\n");
		sb.append("serviceName=").append(serviceName).append("\n");
		sb.append("endPoint=").append(endPoint).append("\n");
		sb.append("returnValue=").append("ReturnValue will not be disclosed.").append("\n");
		
		return sb.toString();
	}
}
