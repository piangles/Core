package com.TBD.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.util.stream.Collectors;

public class SystemHelper
{
	public static final String LOCAL_HOST = "localhost";
	public static final String UNKNOWN_HOST = "Unknown HostName";
	
	public static String getHostName()
	{
		String hostName = null;
		try
		{
			hostName = InetAddress.getLocalHost().getCanonicalHostName();
			if (LOCAL_HOST.equals(hostName))
			{
				hostName = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("hostname").getInputStream())).lines().collect(Collectors.joining("\n"));
			}
		}
		catch (IOException e)
		{
			hostName = UNKNOWN_HOST;
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
