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

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathRequest;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

public final class AWSParameterStoreCentralClient extends CentralClient
{
	private SsmClient ssmClient = null;
	private Environment environment = null;

	public AWSParameterStoreCentralClient() throws Exception
	{
		environment = new Environment(); 
		try
		{
			ssmClient = SsmClient.builder().region(environment.getRegion()).build();
		}
		catch (Exception e)
		{
			String message = "AWS SSMClient could not be created. Reason: " + e.getMessage();
			Logger.getInstance().fatal(message, e);
			throw new Exception(message);
		}
	}

	@Override
	public Properties discover(String serviceName) throws Exception
	{
		Logger.getInstance().info("CentralClient:Discovering properties for Service : " + serviceName);
		return getProperties("Discovery", serviceName);
	}

	@Override
	public Properties tier1Config(String serviceName) throws Exception
	{
		Logger.getInstance().info("CentralClient:Retriving tier1Config for Service : " + serviceName);
		return getProperties("Tier1Config", serviceName);
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

	private Properties getProperties(String propertyName, String serviceName) throws Exception
	{
		Properties discoveryProps = new Properties();
		StringBuffer response = new StringBuffer();
		GetParametersByPathResponse parametersByPathResponse = null;
		GetParametersByPathRequest parametersByPathRequest = null;

		try
		{
			String pathPrefix = "/" + environment.identifyEnvironment() + "/" + propertyName + "/" + serviceName;
			
			Logger.getInstance().info("CentralClient:Searching for pathPrefix : " + pathPrefix);

			

			/**GetParametersByPath is a paged operation. 
			 *After each call you must retrieve NextToken from the result object, and if it's 
			 *not null and not empty you must make another call with it added to the request.
			**/
			do
			{
				parametersByPathRequest = GetParametersByPathRequest.builder()
																	.path(pathPrefix)
																	.recursive(true)
																	.withDecryption(true)
																	.maxResults(10)
																	.nextToken((parametersByPathResponse != null)?parametersByPathResponse.nextToken():null)
																	.build();
				
				parametersByPathResponse = ssmClient.getParametersByPath(parametersByPathRequest);

				if (parametersByPathResponse.parameters().size() != 0)
				{
					for (int i = 0; i < parametersByPathResponse.parameters().size(); ++i)
					{
						Parameter parameter = parametersByPathResponse.parameters().get(i);

						String[] uriSegments = parameter.name().split("/");

						String parameterName = uriSegments[uriSegments.length - 1];

						discoveryProps.put(parameterName, parameter.value());
					}
				}
				else
				{
					response.append("The pathPrefix [" + pathPrefix + "] did yield any search results.");
				}
			} while (StringUtils.isNotBlank(parametersByPathResponse.nextToken()));
		}
		catch (Exception e)
		{
			throw new Exception(e.getMessage(), e);
		}

		if (discoveryProps.isEmpty())
		{
			throw new Exception("Unable to retrieve " + propertyName + " Properties for : " + serviceName + " Because : " + response.toString());
		}

		return discoveryProps;
	}
}
