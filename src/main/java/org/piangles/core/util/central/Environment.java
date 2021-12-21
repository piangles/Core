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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.piangles.core.util.Logger;

import com.amazonaws.regions.Regions;

import software.amazon.awssdk.regions.Region;

public final class Environment
{
	public static final String AWS_REGION_1 = "aws.region";
	public static final String AWS_REGION_2 = "AWS_REGION";
	public static final String PIANGLES_ENV = "piangles_env";
	
	private Map<String, String> environmentCIDRBlockMap = null;

	public Environment()
	{
		environmentCIDRBlockMap = new HashMap<>();
		environmentCIDRBlockMap.put("dev", "172.17.0.0/16");
		environmentCIDRBlockMap.put("qat", "172.18.0.0/16");
		environmentCIDRBlockMap.put("uat", "192.168.0.0/16");
		environmentCIDRBlockMap.put("prod", "10.100.0.0/16");
	}
	
	public Region getRegion() throws Exception
	{
		Region region = null;
		String awsRegion1 = System.getenv(AWS_REGION_1);
		String awsRegion2 = System.getenv(AWS_REGION_2);

		String awsRegion = null;
		if (StringUtils.isAllBlank(awsRegion1, awsRegion2))
		{
			awsRegion = Regions.getCurrentRegion().getName();
			Logger.getInstance().info("Defaulting to Region : " + awsRegion);
		}
		else
		{
			awsRegion = StringUtils.isNotBlank(awsRegion1) ? awsRegion1 : awsRegion2;
			Logger.getInstance().info("Configured to Region : " + awsRegion);
		}

		if (StringUtils.isBlank(awsRegion))
		{
			String message = "AWS Region is either missing or cannot be dertermined.";
			Logger.getInstance().fatal(message);
			throw new Exception(message);
		}
		region = Region.of(awsRegion);

		return region;
	}
	
	public String identifyEnvironment() throws Exception
	{
		String environment = null;
		
		environment = System.getenv(PIANGLES_ENV);
		
		if (StringUtils.isBlank(environment))
		{
			InetAddress inetAddress = null;
			String ipAddress = null;
			try
			{
				inetAddress = InetAddress.getLocalHost();
				ipAddress = inetAddress.getHostAddress();
				Logger.getInstance().info("Hostname: " + inetAddress.getCanonicalHostName() + " IPAddress: " + ipAddress);
			}
			catch (UnknownHostException e)
			{
				String message = "Failed to obtain network details for localhost: " + inetAddress + ". Reason: " + e.getMessage();
				Logger.getInstance().fatal(message, e);
				throw new Exception(message);
			}
			
			for (String env: environmentCIDRBlockMap.keySet())
			{
				String cidrBlock = environmentCIDRBlockMap.get(env);
				SubnetUtils subnetUtils = new SubnetUtils(cidrBlock);
				if (subnetUtils.getInfo().isInRange(ipAddress))
				{
					environment = env;
					break;
				}
			}
			
			if (environment == null)
			{
				String message = "Unable to determine Environment for Hostname: " + inetAddress.getCanonicalHostName() + " IPAddress: " + ipAddress;
				Logger.getInstance().fatal(message);
				throw new Exception(message);
			}
			
			Logger.getInstance().info("Environment for Hostname: " + inetAddress.getCanonicalHostName() + " IPAddress: " + ipAddress + " is: [" + environment + "]");
 		}
		else
		{
			Logger.getInstance().info("PIANGLES_ENV Environment variable set to : [" + environment + "]");
		}
		
		return environment;
	}
}
