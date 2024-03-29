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
