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

/**
 * For Internal Use Only.
 * 
 * This class captures at Trie level performance metrics
 * 1. DataSet Size
 * 2. Memory Usage
 *
 */
public final class TrieStatistics implements Serializable, Cloneable
{
	private static final long serialVersionUID = 1L;

	private String trieName;
	private int datasetSize;
	private int derivedDatasetSize;
	private int skippedStopWordsSize;

	private long timeTakenToGetReady;
	private long timeTakenToSortDataset;
	private long timeTakenToPopulateTrie;
	private long timeTakenToIndex;
	
	private final AtomicLong noOfNodes = new AtomicLong(1);
	private final AtomicLong noOfCalls = new AtomicLong();
	private final AtomicLong noOfCallsWithoutResults = new AtomicLong();
	
	private final AtomicLong runningSumOfQueryStringLength = new AtomicLong();
	
	private final AtomicLong totalResponseTime = new AtomicLong();
	
	private final AtomicReference<String> minQueryString = new AtomicReference<>(); 
	private final LongAccumulator minResponseTime = new LongAccumulator(Long::min, Long.MAX_VALUE);
	
	private final AtomicReference<String> maxQueryString = new AtomicReference<>();
	private final LongAccumulator maxResponseTime = new LongAccumulator(Long::max, 0);
	
	TrieStatistics(String trieName)
	{
		this.trieName = trieName;
	}

	void merge(TrieStatistics other)
	{
		this.datasetSize += other.datasetSize;
		this.derivedDatasetSize += other.derivedDatasetSize;
		this.skippedStopWordsSize += other.skippedStopWordsSize;

		this.noOfCalls.addAndGet(other.noOfCalls.get());
		this.noOfCalls.set(this.noOfCalls.get()/2);
		
		this.noOfCallsWithoutResults.addAndGet(other.noOfCallsWithoutResults.get());
		this.noOfCallsWithoutResults.set(this.noOfCallsWithoutResults.get()/2);
		
		this.runningSumOfQueryStringLength.addAndGet(other.runningSumOfQueryStringLength.get());
		this.runningSumOfQueryStringLength.set(this.runningSumOfQueryStringLength.get()/2);
		
		this.totalResponseTime.addAndGet(other.totalResponseTime.get());
		this.totalResponseTime.set(this.totalResponseTime.get()/2);
		
		this.minResponseTime.accumulate(other.minResponseTime.get());
		if (this.minResponseTime.get() == other.minResponseTime.get())
		{
			minQueryString.set(other.minQueryString.get());
		}

		this.maxResponseTime.accumulate(other.maxResponseTime.get());
		if (this.maxResponseTime.get() == other.maxResponseTime.get())
		{
			maxQueryString.set(other.maxQueryString.get());
		}
		
	
		this.timeTakenToGetReady += other.timeTakenToGetReady;
		this.timeTakenToGetReady /= 2;
		
		this.timeTakenToSortDataset += other.timeTakenToSortDataset;
		this.timeTakenToSortDataset /= 2;
		
		this.timeTakenToPopulateTrie += other.timeTakenToPopulateTrie;
		this.timeTakenToPopulateTrie /= 2;
		
		this.timeTakenToIndex += other.timeTakenToIndex;
		this.timeTakenToIndex /= 2;
	}

	void clear()
	{
		noOfCalls.set(0);
		noOfCallsWithoutResults.set(0);
		
		runningSumOfQueryStringLength.set(0);;
		totalResponseTime.set(0);
		
		minResponseTime.reset();
		maxResponseTime.reset();
	}
	
	public String getName()
	{
		return trieName;
	}

	public long getNoOfNodes()
	{
		return noOfNodes.get();
	}

	public long getNoOfCalls()
	{
		return noOfCalls.get();
	}
	
	public int getDatasetSize()
	{
		return datasetSize;
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

	public long getAverageQueryStringLength()
	{
		long averageQueryStringLength = 0;
		if (noOfCalls.get() != 0)
		{
			averageQueryStringLength = runningSumOfQueryStringLength.get() / noOfCalls.get();			
		}
		return averageQueryStringLength;
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

	void incrementNodeCount()
	{
		noOfNodes.incrementAndGet();
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
		runningSumOfQueryStringLength.addAndGet(queryString.length());
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
	protected Object clone() throws CloneNotSupportedException
	{
		TrieStatistics clonedStats = (TrieStatistics)super.clone();
		clonedStats.trieName = "Consolidated Trie Stats";
		
		return clonedStats;
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		NumberFormat nf = NumberFormat.getNumberInstance();
		sb.append("TrieStatistics for: " + trieName).append("\n");
		sb.append("-------------------\n");
		sb.append("Complete Dataset Size: ").append(nf.format(datasetSize)).append(" words.\n");
		sb.append("Derived Subset Size: ").append(nf.format(derivedDatasetSize)).append(" words.\n");
		sb.append("Skipped StopWords Size: ").append(nf.format(skippedStopWordsSize)).append(" words.\n");
		sb.append("Time Taken to sort: ").append(timeTakenToSortDataset).append(" in MiliSeconds.").append("\n");
		sb.append("Time Taken to populate Trie(s): ").append(timeTakenToPopulateTrie).append(" in MiliSeconds.").append("\n");
		sb.append("Time Taken to index Trie: ").append(timeTakenToIndex).append(" in MiliSeconds.").append("\n");
		sb.append("Time Taken to for Trie to be Ready: ").append(timeTakenToGetReady).append(" in MiliSeconds.").append("\n");
		sb.append("Total No. Of Nodes: ").append(nf.format(noOfNodes)).append(".\n");
		sb.append("Total No. Of Calls: ").append(nf.format(noOfCalls)).append(".\n");
		sb.append("No. Of Calls not yielding Results: ").append(nf.format(noOfCallsWithoutResults)).append(".\n");
		sb.append("Minimum Response Time: ").append(nf.format(minResponseTime)).append(" NanoSeconds. Query String: ").append(minQueryString).append("\n");
		sb.append("Maximum Response Time: ").append(nf.format(maxResponseTime)).append(" NanoSeconds. Query String: ").append(maxQueryString).append("\n");
		sb.append("Average Response Time: ").append(nf.format(getAverageResponseTime())).append(" NanoSeconds.").append("\n");
		sb.append("Average Length of QueryString: ").append(nf.format(getAverageQueryStringLength())).append(" Characters.").append("\n");

		return sb.toString();
	}
}
