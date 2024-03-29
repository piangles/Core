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
 
 
 
package org.piangles.core.util.central;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.piangles.core.util.Logger;

public abstract class CentralClient
{
	private static final String CENTRAL_CLIENT_CLASS_1 = "central.client.class";
	private static final String CENTRAL_CLIENT_CLASS_2 = "central_client_class";
	
	private static CentralClient centralClient = null; 
	
	public static CentralClient getInstance()
	{
		if (centralClient == null)
		{
			synchronized (CentralClient.class)
			{
				if (centralClient == null)
				{
					try
					{
						String centralClientClassName1 = System.getenv(CENTRAL_CLIENT_CLASS_1);
						String centralClientClassName2 = System.getenv(CENTRAL_CLIENT_CLASS_2);
						
						String centralClientClassName = null;
						if (StringUtils.isAllBlank(centralClientClassName1, centralClientClassName2))
						{
							centralClientClassName = DefaultCentralClient.class.getCanonicalName();
							Logger.getInstance().warn(CENTRAL_CLIENT_CLASS_1 + " and " + CENTRAL_CLIENT_CLASS_2 + " property is NOT set, defaulting to : " + centralClientClassName);
						}
						else
						{
							centralClientClassName = StringUtils.isNotBlank(centralClientClassName1)? centralClientClassName1 : centralClientClassName2;
							Logger.getInstance().info(CENTRAL_CLIENT_CLASS_1 + " or " + CENTRAL_CLIENT_CLASS_2 + " property is set, trying to create : " + centralClientClassName);
						}

						centralClient = (CentralClient)Class.forName(centralClientClassName).newInstance();
					}
					catch(Throwable t)
					{
						throw new Error(t);
					}
				}
			}
		}
		return centralClient;
	}
	
	public abstract Properties discover(String serviceName) throws Exception;
	public abstract Properties tier1Config(String serviceName) throws Exception;
	public abstract String decrypt(String serviceName, String encryptedCategory, String encryptedValueName, String encryptedValue, 
			String cipherAuthorizationIdName, String cipherAuthorizationId)throws Exception;
}
