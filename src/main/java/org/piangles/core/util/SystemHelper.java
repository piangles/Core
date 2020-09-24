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
