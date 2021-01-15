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
 
 
 
package org.piangles.core.test;

import org.piangles.core.services.remoting.SessionAwareable;
import org.piangles.core.services.remoting.SessionDetails;

public abstract class AbstractServiceTestClient extends Thread implements SessionAwareable
{
	protected static String cipherAuthorizationId = "7a948dce-1ebb-4770-b077-f453e60243da";

	public final void run()
	{
		try
		{
			runImpl();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.exit(1);
	}
	
	@Override
	public final SessionDetails getSessionDetails()
	{
		return new SessionDetails("FeaturesTestService", "TODOSessionId");
	}
	
	public abstract void runImpl() throws Exception;
}
