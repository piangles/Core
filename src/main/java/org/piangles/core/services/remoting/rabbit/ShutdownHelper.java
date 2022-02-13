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
 
 
package org.piangles.core.services.remoting.rabbit;

import org.piangles.core.util.Logger;

import com.rabbitmq.client.ShutdownSignalException;

public final class ShutdownHelper
{
	public static boolean process(String serviceName, String controllerType, ShutdownSignalException exception)
	{
		boolean keepListening = true;

		keepListening = !exception.isInitiatedByApplication();
		
		String reference = "No Reference Given.";
		if (exception.getReference() != null)
		{
			reference = exception.getReference().getClass().getCanonicalName();
		}
	
		Logger.getInstance().warn(controllerType + " for Service: " + serviceName 
		+ " has exited RabbitMQ->RpcServer. Reason: " + exception.getMessage()
		+ " isHardError: " + exception.isHardError()
		+ " isInitiatedByApplication: " + exception.isInitiatedByApplication()
		+ " Reference: " + reference, exception);
		
		if (exception.getReason() != null)
		{
			Logger.getInstance().warn("ShutdownSignalException for Service: " + serviceName + ". Reason: " 
					+ " protocolClassId:" + exception.getReason().protocolClassId() 
					+ " protocolMethodId:" + exception.getReason().protocolMethodId()
					+ " protocolMethodName:" + exception.getReason().protocolMethodName());
		}
		else
		{
			Logger.getInstance().warn("ShutdownSignalException for Service: " + serviceName + ". Without a Reason."); 
		}

		return keepListening;
	}
}
