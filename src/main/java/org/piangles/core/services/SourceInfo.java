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

public final class SourceInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String className = null;
	private String lineNumber = null;
	private String stackTrace = null;
	private String authorizationId = null;
	
	public SourceInfo(String className, String lineNumber, String stackTrace, String authorizationId)
	{
		super();
		this.className = className;
		this.lineNumber = lineNumber;
		this.stackTrace = stackTrace;
		this.authorizationId = authorizationId;
	}
	
	public String getClassName()
	{
		return className;
	}
	
	public String getLineNumber()
	{
		return lineNumber;
	}
	
	public String getStackTrace()
	{
		return stackTrace;
	}

	public String getAuthorizationId()
	{
		return authorizationId;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("AuthorizationId=").append(authorizationId).append("\n");
		sb.append("className=").append(className).append("\n");
		sb.append("lineNumber=").append(lineNumber).append("\n");
		sb.append("stackTrace=").append("\n").append(stackTrace).append("\n");
		
		return sb.toString();
	}
}
