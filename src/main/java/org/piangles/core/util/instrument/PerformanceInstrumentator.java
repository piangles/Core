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

package org.piangles.core.util.instrument;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

@SuppressWarnings("restriction")
public final class PerformanceInstrumentator extends AbstractInstrumentator
{
	private static final String NAME = "PerformanceDetails";
	private OperatingSystemMXBean osMXBean = null;

	private Measures performanceDetails = null;

	public PerformanceInstrumentator(String serviceName)
	{
		super(NAME);

		osMXBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();

		performanceDetails = new Measures(NAME, serviceName);
	}

	@Override
	public Measures doInstrumentation()
	{
		performanceDetails.clear();

		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		int availableProcessors = osMXBean.getAvailableProcessors();
		long prevUpTime = runtimeMXBean.getUptime();
		long prevProcessCpuTime = osMXBean.getProcessCpuTime();
		double cpuUsage;
		try
		{
			Thread.sleep(500);
		}
		catch (Exception ignored)
		{
		}

		long upTime = runtimeMXBean.getUptime();
		long processCpuTime = osMXBean.getProcessCpuTime();
		long elapsedCpu = processCpuTime - prevProcessCpuTime;
		long elapsedTime = upTime - prevUpTime;

		cpuUsage = Math.min(99F, elapsedCpu / (elapsedTime * 10000F * availableProcessors));
		
		performanceDetails.addMeasure("AverageSystemLoad", osMXBean.getSystemLoadAverage());
		performanceDetails.addMeasure("CpuUsage", cpuUsage);
		
		return performanceDetails;
	}
}
