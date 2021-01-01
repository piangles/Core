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
 
 
 
package org.piangles.core.resources;

import java.util.Properties;

final class RMQProperties
{
	private static final String HOST = "RMQHostName";
	private static final String PORT = "RMQPort";
	static final String LOGIN = "RMQLoginId";
	static final String PASSWORD = "RMQPassword";
	
	private Properties properties = null;
	
	public RMQProperties(Properties properties)
	{
		this.properties = properties;
	}

	public String getHost()
	{
		return properties.getProperty(HOST);
	}

	public int getPort()
	{
		return Integer.valueOf(properties.getProperty(PORT));
	}

	public String getLogin()
	{
		return properties.getProperty(LOGIN);
	}

	public String getPassword()
	{
		return properties.getProperty(PASSWORD);
	}
}
