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
 
 
 
package org.piangles.core.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SystemHelper
{
	public static String getHostName()
	{
		String hostName = null;
		try
		{
			hostName = InetAddress.getLocalHost().getCanonicalHostName();
		}
		catch (UnknownHostException e)
		{
			hostName = "Unknown HostName";
		}
		return hostName;
	}
	
	public static String getLoginId()
	{
		return System.getProperty("user.name");
	}
	
	public static String getProcessName()
	{
		String processName = System.getProperty("process.name");
		if (processName == null || processName.trim().length() == 0)
		{
			processName = "process.name not defined";
		}
		return processName;
	}
	 
	public static String getProcessId()
	{
		String processId = "Unknown Process";
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		if (runtimeBean != null && runtimeBean.getName() != null)
		{
			String[] processIdParts = runtimeBean.getName().split("@");
			if (processIdParts != null && processIdParts.length >= 1)
			{
				processId = processIdParts[0];
			}
		}
		return processId;
	}
	
	public static String getThreadId()
	{
		return Thread.currentThread().getName();
	}
}
