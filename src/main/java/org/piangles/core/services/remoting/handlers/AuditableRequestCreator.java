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
 
 
 
package org.piangles.core.services.remoting.handlers;

import java.lang.reflect.Method;
import java.util.UUID;

import org.piangles.core.services.AuditDetails;
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
		if (args[0] instanceof AuditDetails)
		{
			AuditDetails auditDetails = (AuditDetails) args[0];
			header = auditDetails.getHeader();
			sourceInfo = auditDetails.getSourceInfo();
			
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
