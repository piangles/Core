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

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

public final class ServicePerformanceDetails
{
	public static final String NAME = "ServicePerformanceDetails";
	
	private Date lastRequestTime = null;
	private String lastRequestTraceId = null;
	
	private final LongAdder noOfRequests = new LongAdder();
	private final LongAdder noOfSuccessfulResponses = new LongAdder();
	private final LongAdder noOfFailedResponses = new LongAdder();
	
	private final String minLock = new String("MinLock");
	private final AtomicReference<String> minResponseTraceId = new AtomicReference<>(); 
	private final LongAccumulator minResponseTime = new LongAccumulator(Long::min, Long.MAX_VALUE);
	
	private final String maxLock = new String("MaxLock");
	private final AtomicReference<String> maxResponseTraceId = new AtomicReference<>();
	private final LongAccumulator maxResponseTime = new LongAccumulator(Long::max, 0);

	private final LongAdder totalResponseTime = new LongAdder();

	public ServicePerformanceDetails()
	{
		
	}
	
	public void incrementNoOfRequests()
	{
		noOfRequests.increment();
	}

	public void setLastRequest(String traceId)
	{
		lastRequestTraceId = traceId;
		lastRequestTime = new Date();	
	}
	

	public void incrementNoOfSuccessfulResponses()
	{
		noOfSuccessfulResponses.increment();
	}

	public void incrementNoOfFailedResponses()
	{
		noOfFailedResponses.increment();
	}
	
	public Date getLastRequestTime()
	{
		return lastRequestTime; 
	}

	public String getLastRequestTraceId()
	{
		return lastRequestTraceId; 
	}

	public long getNoOfRequests()
	{
		return noOfRequests.longValue();
	}

	public long getNoOfSuccessfulResponses()
	{
		return noOfSuccessfulResponses.longValue();
	}

	public long getNoOfFailedResponses()
	{
		return noOfFailedResponses.longValue();
	}
	
	public long getAverageResponseTime()
	{
		long averageResponseTime = 0;
		if (noOfRequests.longValue() != 0)
		{
			averageResponseTime = totalResponseTime.longValue() / noOfRequests.longValue();
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

	public void record(String traceId, long timeTakenForRequestToResponseNS)
	{
		totalResponseTime.add(timeTakenForRequestToResponseNS);

		synchronized(minLock)
		{
			minResponseTime.accumulate(timeTakenForRequestToResponseNS);
			if (minResponseTime.get() == timeTakenForRequestToResponseNS)
			{
				minResponseTraceId.set(traceId);
			}
		}

		synchronized(maxLock)
		{
			maxResponseTime.accumulate(timeTakenForRequestToResponseNS);
			if (maxResponseTime.get() == timeTakenForRequestToResponseNS)
			{
				maxResponseTraceId.set(traceId);
			}
		}
	}
}
