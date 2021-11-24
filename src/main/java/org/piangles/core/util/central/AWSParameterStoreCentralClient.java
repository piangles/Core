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

import com.amazonaws.regions.Regions;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathRequest;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

public final class AWSParameterStoreCentralClient extends CentralClient
{
	public static final String AWS_REGION = "aws.region";

	private SsmClient ssmClient = null;

	public AWSParameterStoreCentralClient() throws Exception
	{
		Region region = null;
		String awsRegion = System.getenv(AWS_REGION);

		if (awsRegion == null)
		{
			awsRegion = Regions.getCurrentRegion().getName();
		}

		if (StringUtils.isBlank(awsRegion))
		{
			String message = "AWS Region is either missing or cannot be dertermined.";
			Logger.getInstance().fatal(message);
			throw new Exception(message);
		}
		region = Region.of(awsRegion);

		try
		{
			ssmClient = SsmClient.builder().region(region).build();
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
		Properties discoveryProps = null;
		StringBuffer response = new StringBuffer();

		try
		{
			String pathPrefix = "/" + identifyEnvironment() + "/" + propertyName + "/" + serviceName;

			GetParametersByPathRequest parametersByPathRequest = GetParametersByPathRequest.builder()
																	.path(pathPrefix)
																	.recursive(true)
																	.withDecryption(true)
																	.build();

			GetParametersByPathResponse parametersByPathResponse = ssmClient.getParametersByPath(parametersByPathRequest);

			if (parametersByPathResponse.parameters().size() != 0)
			{
				discoveryProps = new Properties();
				for (int i=0; i < parametersByPathResponse.parameters().size(); ++i)
				{
					Parameter parameter = parametersByPathResponse.parameters().get(i);
					
					String[] uriSegments = parameter.name().split("/");
					
					String parameterName = uriSegments[uriSegments.length - 1];
					
					discoveryProps.put(parameterName, parameter.value());
				}
			}
		}
		catch (Exception e)
		{
			throw new Exception(e.getMessage(), e);
		}

		if (discoveryProps == null)
		{
			throw new Exception("Unable to retrieve " + propertyName + " Properties for : " + serviceName + " Because : " + response.toString());
		}

		return discoveryProps;
	}
	
	private String identifyEnvironment()
	{
		return "dev";
	}
}
