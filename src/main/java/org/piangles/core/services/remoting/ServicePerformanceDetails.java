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

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAccumulator;

public final class ServicePerformanceDetails
{
	public static final String NAME = "ServicePerformanceDetails";
	
	private final AtomicLong noOfRequests = new AtomicLong(0);
	private final AtomicLong noOfSuccessfulResponses = new AtomicLong(0);
	private final AtomicLong noOfFailedResponses = new AtomicLong(0);
	
	private final AtomicReference<String> minResponseTraceId = new AtomicReference<>(); 
	private final LongAccumulator minResponseTime = new LongAccumulator(Long::min, Long.MAX_VALUE);
	
	private final AtomicReference<String> maxResponseTraceId = new AtomicReference<>();
	private final LongAccumulator maxResponseTime = new LongAccumulator(Long::max, 0);

	private final AtomicLong totalResponseTime = new AtomicLong();

	public ServicePerformanceDetails()
	{
		
	}
	
	public void incrementNoOfRequests()
	{
		noOfRequests.incrementAndGet();
	}

	public void incrementNoOfSuccessfulResponses()
	{
		noOfSuccessfulResponses.incrementAndGet();
	}

	public void incrementNoOfFailedResponses()
	{
		noOfFailedResponses.incrementAndGet();
	}
	
	public long getNoOfRequests()
	{
		return noOfRequests.get();
	}

	public long getNoOfSuccessfulResponses()
	{
		return noOfSuccessfulResponses.get();
	}

	public long getNoOfFailedResponses()
	{
		return noOfFailedResponses.get();
	}
	
	public long getAverageResponseTime()
	{
		long averageResponseTime = 0;
		if (noOfRequests.get() != 0)
		{
			averageResponseTime = totalResponseTime.get() / noOfRequests.get();
		}
		return averageResponseTime;
	}

	public long getMinResponseTime()
	{
		return minResponseTime.get();
	}

	public String getMinResponseTraceId()
	{
		return minResponseTraceId.get();
	}

	public long getMaxResponseTime()
	{
		return maxResponseTime.get();
	}

	public String getMaxResponseTraceId()
	{
		return maxResponseTraceId.get();
	}

	public void record(String traceId, long responseTime)
	{
		totalResponseTime.addAndGet(responseTime);

		minResponseTime.accumulate(responseTime);
		if (minResponseTime.get() == responseTime)
		{
			minResponseTraceId.set(traceId);
		}
		maxResponseTime.accumulate(responseTime);
		if (maxResponseTime.get() == responseTime)
		{
			maxResponseTraceId.set(traceId);
		}
	}
}
