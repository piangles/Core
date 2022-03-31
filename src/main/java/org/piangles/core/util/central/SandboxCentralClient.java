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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.piangles.core.expt.ServiceRuntimeException;
import org.piangles.core.util.Logger;

public final class SandboxCentralClient extends CentralClient
{
	private static final String DISCOVERY_STORE = "discovery.store";
	private static final String TIER1CONFIG_STORE = "tier1Config.store";

	private Map<String, Properties> discoveryStore = null;
	private Map<String, Properties> tier1ConfigStore = null;

	public SandboxCentralClient() throws Exception
	{
		discoveryStore = loadFromStore("Discovery", DISCOVERY_STORE);

		tier1ConfigStore = loadFromStore("Tier1Config", TIER1CONFIG_STORE);
	}

	@Override
	public Properties discover(String serviceName) throws Exception
	{
		Logger.getInstance().info("SandboxCentralClient:Discovering properties for Service : " + serviceName);
		return discoveryStore.get(serviceName);
	}

	@Override
	public Properties tier1Config(String serviceName) throws Exception
	{
		Logger.getInstance().info("SandboxCentralClient:Retriving tier1Config for Service : " + serviceName);
		return tier1ConfigStore.get(serviceName);
	}

	@Override
	public String decrypt(String serviceName, String encryptedCategory, String encryptedValueName, String encryptedValue, String cipherAuthorizationIdName, String cipherAuthorizationId)
			throws Exception
	{
		/**
		 * TODO AWS to Decrypt
		 */
		return encryptedValue;
	}

	private Map<String, Properties> loadFromStore(String specifiedCategory, String envVariable)
	{
		/**
		 * File format for each type is below.
		 * 
		 * Discovery: Environment,Category,ServiceName,Name,Value
		 * Tier1Config: Environment,Category,ServiceName,Name,Value
		 */
		Map<String, Properties> propsStore = null;

		String storeFile = System.getenv(envVariable);
		Logger.getInstance().info("EnvironmentVariable: " + envVariable + " Value: " + storeFile);
		
		if (StringUtils.isBlank(storeFile))
		{
			throw new ServiceRuntimeException("Empty/Null environment value for variable: " + envVariable);
		}

		
		try (BufferedReader br = new BufferedReader(new FileReader(storeFile)))
		{
			propsStore = new HashMap<>();

			Properties serviceProps = null;
			String header = null;
			String line = null;
			String env, category, serviceName, name, value;
			env = category = serviceName = name = value = null;
			
			while ((line = br.readLine()) != null)
			{
				line = StringUtils.trimToNull(line);
				if (line == null)
				{
					continue;
				}
				
				if (header == null)
				{
					header = line;
					Logger.getInstance().info("Header: " + header);
					continue;
				}
				
				// process the line.
				String[] params = line.split(",");
				
				env = params[0];
				category = params[1];
				serviceName = params[2];
				name = params[3];
				value = params[4];

				if (!Environment.Sandbox.equals(env))
				{
					throw new ServiceRuntimeException("Invalid environment in Configuration. Only sandbox is supported."); 
				}
				else if (!specifiedCategory.equals(category))
				{
					throw new ServiceRuntimeException("Invalid category in Configuration. Only " + specifiedCategory + " is supported."); 
				}
	
				serviceProps = propsStore.get(serviceName);
				if (serviceProps == null)
				{
					serviceProps = new Properties();
					propsStore.put(serviceName, serviceProps);
				}
				
				serviceProps.put(name, value);
			}
		}
		catch (IOException e)
		{
			String message = "LocalStoreCentralClient could not be created. Reason: " + e.getMessage();
			Logger.getInstance().fatal(message, e);

			throw new ServiceRuntimeException(message, e);
		}

		return propsStore;
	}
}
