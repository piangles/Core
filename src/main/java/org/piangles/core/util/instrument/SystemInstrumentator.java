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
import java.lang.management.OperatingSystemMXBean;

public final class SystemInstrumentator extends AbstractInstrumentator
{
	private static final String NAME = "SystemDetails";
	private Measures systemDetails = null;
	
	public SystemInstrumentator(String serviceName)
	{
		super(NAME);
		
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

		systemDetails = new Measures(NAME, serviceName, true);
		systemDetails.addMeasure("OSName", operatingSystemMXBean.getName());
		systemDetails.addMeasure("OSVersion", operatingSystemMXBean.getVersion());
		systemDetails.addMeasure("Architecture", operatingSystemMXBean.getArch());
		systemDetails.addMeasure("AvailableProcessors", operatingSystemMXBean.getAvailableProcessors());
	}
	
	@Override
	public Measures doInstrumentation()
	{
		return systemDetails;
	}
}
