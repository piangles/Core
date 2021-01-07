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
	private String context;
	private int datasetSize;
	
	private long noOfCalls;
	private long noOfCallsWithoutResults;
	private long averageResponseTime;
	private long averageSearchWordSize;

	//Metrics
	private long timeTakenToGetReady;
	private long timeTakenToSortDataset;
	private long timeTakenToPopulateTrie;
	private long timeTakenToIndex;
	
	private int maxMemoryInMB;
	private int totalMemoryInMB;
	private int freeMemoryInMB;
	private int usedMemoryInMB;
	
	TrieStatistics(String context)
	{
		this.context = context;
	}
	
	public String getContext()
	{
		return context;
	}
	
	public void incrementDatasetSize()
	{
		datasetSize++;
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
		sb.append("Time Taken to sort: " + timeTakenToSortDataset + " in MiliSeconds.").append("\n");
		sb.append("Time Taken to populate Trie: " + timeTakenToPopulateTrie + " in MiliSeconds.").append("\n");
		sb.append("Time Taken to index Trie: " + timeTakenToIndex + " in MiliSeconds.").append("\n");
		sb.append("Time Taken to for Trie to be Ready: " + timeTakenToGetReady + " in MiliSeconds.").append("\n");

		return sb.toString();
	}
	
	public void memory()
	{
		int mb = 1024*1024;
		
		//Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();
		
		System.out.println("##### Heap utilization statistics [MB] #####");
		
		//Print used memory
		System.out.println("Used Memory:" 
			+ (runtime.totalMemory() - runtime.freeMemory()) / mb);

		//Print free memory
		System.out.println("Free Memory:" 
			+ runtime.freeMemory() / mb);
		
		//Print total available memory
		System.out.println("Total Memory:" + runtime.totalMemory() / mb);

		//Print Maximum available memory
		System.out.println("Max Memory:" + runtime.maxMemory() / mb);		
	}
}
