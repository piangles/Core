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

import java.util.Properties;

import org.piangles.core.util.central.CentralClient;

public final class SessionDetailsCreator
{
	private static final String PRE_APPROVED_SESSIONID = "PreApprovedSessionId";
	
	
	public static SessionDetails createSessionDetails(String serviceName) throws Exception
	{
		return createSessionDetails(serviceName, CentralClient.getInstance().discover(serviceName));
	}
	
	static SessionDetails createSessionDetails(String serviceName, Properties props) throws Exception
	{
		return new SessionDetails(serviceName, props.getProperty(PRE_APPROVED_SESSIONID));
	}
}
