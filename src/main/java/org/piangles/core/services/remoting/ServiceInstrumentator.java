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

package org.piangles.core.services.remoting;

import java.util.concurrent.TimeUnit;

import org.piangles.core.util.instrument.AbstractInstrumentator;
import org.piangles.core.util.instrument.Measures;

public final class ServiceInstrumentator extends AbstractInstrumentator
{
	private ServicePerformanceDetails spDetails = null;
	private Measures spdMeasures = null;
	
	public ServiceInstrumentator(String serviceName)
	{
		super(ServicePerformanceDetails.NAME);
		
		spDetails = new ServicePerformanceDetails(); 
		spdMeasures = new Measures(ServicePerformanceDetails.NAME, serviceName, false);
	}
	
	public ServicePerformanceDetails getServicePerformanceDetails()
	{
		return spDetails;
	}
	
	@Override
	public Measures doInstrumentation()
	{
		spdMeasures.clear();
		
		long badRequestCount = spDetails.getNoOfRequests() - spDetails.getNoOfSuccessfulResponses() - spDetails.getNoOfFailedResponses(); 
		
		spdMeasures.addMeasure("NoOfRequests", spDetails.getNoOfRequests());
		
		spdMeasures.addMeasure("LastRequestTime", spDetails.getLastRequestTime());
		spdMeasures.addMeasure("LastRequestTraceId", spDetails.getLastRequestTraceId());
		
		spdMeasures.addMeasure("NoOfSuccessfulResponses", spDetails.getNoOfSuccessfulResponses());
		spdMeasures.addMeasure("NoOfFailedResponses", spDetails.getNoOfFailedResponses());
		spdMeasures.addMeasure("NoOfBadRequests", badRequestCount);

		spdMeasures.addMeasure("AverageResponseTime", "" + TimeUnit.NANOSECONDS.toMillis(spDetails.getAverageResponseTime()) + "Ms");
		
		spdMeasures.addMeasure("MinResponseTime", "" + TimeUnit.NANOSECONDS.toMillis(spDetails.getMinResponseTime()) + "Ms");
		spdMeasures.addMeasure("MinResponseTraceId", spDetails.getMinResponseTraceId());

		spdMeasures.addMeasure("MaxResponseTime", "" + TimeUnit.NANOSECONDS.toMillis(spDetails.getMaxResponseTime()) + "Ms");
		spdMeasures.addMeasure("MaxResponseTraceId", spDetails.getMaxResponseTraceId());

		return spdMeasures;
	}
}
