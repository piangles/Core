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

import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAccumulator;

/**
 * For Internal Use Only.
 * 
 * This class captures at Trie level performance metrics
 * 1. DataSet Size
 * 2. Memory Usage
 *
 */
public final class TrieStatistics
{
	private String trieName;
	private int datasetSize;
	private int derivedDatasetSize;
	private int skippedStopWordsSize;

	private final AtomicLong noOfCalls = new AtomicLong();
	private final AtomicLong noOfCallsWithoutResults = new AtomicLong();
	
	private final LongAccumulator totalQueryWordLength = new LongAccumulator(Long::sum, 0);
	private final LongAccumulator totalResponseTime = new LongAccumulator(Long::sum, 0);
	
	private final AtomicReference<String> minQueryString = new AtomicReference<>(); 
	private final LongAccumulator minResponseTime = new LongAccumulator(Long::min, Long.MAX_VALUE);
	
	private final AtomicReference<String> maxQueryString = new AtomicReference<>();
	private final LongAccumulator maxResponseTime = new LongAccumulator(Long::max, 0);
	
	private long timeTakenToGetReady;
	private long timeTakenToSortDataset;
	private long timeTakenToPopulateTrie;
	private long timeTakenToIndex;
	
	TrieStatistics(String trieName)
	{
		this.trieName = trieName;
	}
	
	void clear()
	{
		noOfCalls.set(0);
		noOfCallsWithoutResults.set(0);
		
		totalQueryWordLength.reset();
		totalResponseTime.reset();
		minResponseTime.reset();
		maxResponseTime.reset();
	}
	
	public String getName()
	{
		return trieName;
	}
	
	public long getCallCount()
	{
		return noOfCalls.get();
	}
	
	public int getDatasetSize()
	{
		return datasetSize;
	}
	
	public long getAverageResponseTime()
	{
		return (totalResponseTime.get() / noOfCalls.get());
	}

	public long getAverageQueryWordLength()
	{
		return (totalQueryWordLength.get() / noOfCalls.get());
	}
	
	void setDatasetSize(int datasetSize)
	{
		this.datasetSize = datasetSize;	
	}

	void incrementDerviedDatasetSize()
	{
		derivedDatasetSize++;
	}

	void incrementSkippedStopWords()
	{
		skippedStopWordsSize++;
	}

	void incrementCallCount()
	{
		noOfCalls.incrementAndGet();
	}

	void incrementEmptyResultCallCount()
	{
		noOfCallsWithoutResults.incrementAndGet();
	}

	void record(String queryString, long responseTime)
	{
		//System.out.println(queryWordSize + " : " + responseTime);
		totalQueryWordLength.accumulate(queryString.length());
		totalResponseTime.accumulate(responseTime);
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
	
	void start(TrieMetrics tm)
	{
		long startTime = System.currentTimeMillis();
		switch(tm)
		{
		case Readiness: timeTakenToGetReady = startTime; break;
		case SortDataset: timeTakenToSortDataset = startTime; break;
		case PopulateTrie: timeTakenToPopulateTrie = startTime; break;
		case IndexTrie: timeTakenToIndex = startTime; break;
		}
	}
	
	void end(TrieMetrics tm)
	{
		long endTime = System.currentTimeMillis();
		switch(tm)
		{
		case Readiness: timeTakenToGetReady = endTime - timeTakenToGetReady; break;
		case SortDataset: timeTakenToSortDataset = endTime - timeTakenToSortDataset; break;
		case PopulateTrie: timeTakenToPopulateTrie = endTime - timeTakenToPopulateTrie; break;
		case IndexTrie: timeTakenToIndex = endTime - timeTakenToIndex; break;
		}
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		NumberFormat nf = NumberFormat.getNumberInstance();
		sb.append("Complete Dataset Size: " + nf.format(datasetSize)).append(" words.\n");
		sb.append("Derived Subset Size: " + nf.format(derivedDatasetSize)).append(" words.\n");
		sb.append("Skipped StopWords Size: " + nf.format(skippedStopWordsSize)).append(" words.\n");
		sb.append("Time Taken to sort: " + timeTakenToSortDataset + " in MiliSeconds.").append("\n");
		sb.append("Time Taken to populate Trie: " + timeTakenToPopulateTrie + " in MiliSeconds.").append("\n");
		sb.append("Time Taken to index Trie: " + timeTakenToIndex + " in MiliSeconds.").append("\n");
		sb.append("Time Taken to for Trie to be Ready: " + timeTakenToGetReady + " in MiliSeconds.").append("\n");
		sb.append("No Of Calls: " + noOfCalls + ".").append("\n");
		sb.append("No Of Calls not yielding Results: " + noOfCallsWithoutResults + ".").append("\n");
		sb.append("Minimum Response Time: " + nf.format(minResponseTime) + " NanoSeconds. Query String: ").append(minQueryString).append("\n");
		sb.append("Maximum Response Time: " + nf.format(maxResponseTime) + " NanoSeconds. Query String: ").append(maxQueryString).append("\n");
		sb.append("Average Response Time: " + nf.format(getAverageResponseTime()) + " NanoSeconds.").append("\n");
		sb.append("Average Query Word Length: " + nf.format(getAverageQueryWordLength()) + " Characters.").append("\n");

		return sb.toString();
	}
}
