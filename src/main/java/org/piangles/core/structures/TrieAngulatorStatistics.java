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
 
 
package org.piangles.core.structures;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAccumulator;

public final class TrieAngulatorStatistics implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String datasetName = null;
	private long timeTakenToIndex;
	
	private final AtomicLong noOfCalls = new AtomicLong();
	
	private final AtomicLong totalResponseTime = new AtomicLong();
	
	private final AtomicReference<String> minQueryString = new AtomicReference<>(); 
	private final LongAccumulator minResponseTime = new LongAccumulator(Long::min, Long.MAX_VALUE);
	
	private final AtomicReference<String> maxQueryString = new AtomicReference<>();
	private final LongAccumulator maxResponseTime = new LongAccumulator(Long::max, 0);

	private TrieStatistics consolidatedTrieStatistics;
	
	TrieAngulatorStatistics(String datasetName)
	{
		this.datasetName = datasetName;
	}
	
	public String getName()
	{
		return datasetName;
	}
	
	public long getTimeTakenToIndex()
	{
		return timeTakenToIndex;
	}

	public long getCallCount()
	{
		return noOfCalls.get();
	}
	
	public long getAverageResponseTime()
	{
		long averageResponseTime = 0;
		if (noOfCalls.get() != 0)
		{
			averageResponseTime = totalResponseTime.get() / noOfCalls.get();
		}
		return averageResponseTime;
	}

	void incrementCallCount()
	{
		noOfCalls.incrementAndGet();
	}

	void setTimeTakenToIndex(long timeTakenToIndex)
	{
		this.timeTakenToIndex = timeTakenToIndex;
	}
	
	void setTrieStatistics(TrieStatistics consolidatedTrieStatistics)
	{
		this.consolidatedTrieStatistics = consolidatedTrieStatistics;
	}
	
	void record(String queryString, long responseTime)
	{
		totalResponseTime.addAndGet(responseTime);
		minResponseTime.accumulate(responseTime);
		if (minResponseTime.get() == responseTime)
		{
			minQueryString.set(queryString);
		}
		maxResponseTime.accumulate(responseTime);
		if (maxResponseTime.get() == responseTime)
		{
			maxQueryString.set(queryString);
		}
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		NumberFormat nf = NumberFormat.getNumberInstance();
		sb.append("TrieAngulator Statistics for: ").append(datasetName).append("\n");
		sb.append("-----------------------------\n");
		sb.append("Total time taken to Index: ").append(timeTakenToIndex).append(" in MiliSeconds.").append("\n");
		sb.append("Total No. Of Calls: ").append(noOfCalls).append(".\n");
		sb.append("Minimum Response Time: ").append(nf.format(minResponseTime)).append(" NanoSeconds. Query String: ").append(minQueryString).append("\n");
		sb.append("Maximum Response Time: ").append(nf.format(maxResponseTime)).append(" NanoSeconds. Query String: ").append(maxQueryString).append("\n");
		sb.append("Average Response Time: ").append(nf.format(getAverageResponseTime())).append(" NanoSeconds.").append("\n");

		sb.append("\n").append(consolidatedTrieStatistics);
		return sb.toString();
	}
	
	
}
