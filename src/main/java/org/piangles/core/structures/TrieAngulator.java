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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class TrieAngulator
{
	private static final String DEFAULT_ATTRIBUTE = "Default";
	
    private String datasetName = null;
    private TrieConfig trieConfig = null;
	
    private HashMap<String, Trie> attributeNameTrieMap = null;
	private Collection<Trie> tries = null;
	
	private int noOfAttributes = 0;
    private ExecutorService executor = null;
	private boolean started = false;
	
	public TrieAngulator(String datasetName, TrieConfig trieConfig)
	{
		this(datasetName, Arrays.asList(new String[]{DEFAULT_ATTRIBUTE}), trieConfig);
	}

	public TrieAngulator(String datasetName, List<String> attributes, TrieConfig trieConfig)
	{
		System.out.println(trieConfig);
		this.datasetName = datasetName;
		this.trieConfig = trieConfig;
		
		noOfAttributes = attributes.size();
		if (noOfAttributes == 1)
		{
			executor = Executors.newSingleThreadExecutor();
		}
		else
		{
			executor = Executors.newFixedThreadPool(noOfAttributes);
		}
		attributeNameTrieMap = new HashMap<>(noOfAttributes);
		for (String attribute : attributes)
		{
			attributeNameTrieMap.put(attribute, new Trie(attribute, trieConfig));
		}
		tries = attributeNameTrieMap.values();
	}
	
	public String getDatasetName()
	{
		return datasetName;
	}

	public void insert(TrieEntry te)
	{
		attributeNameTrieMap.get(DEFAULT_ATTRIBUTE).insert(te);
	}

	public void insert(String attribute, TrieEntry te)
	{
		Trie trie = attributeNameTrieMap.get(attribute);
		if (trie != null)
		{
			trie.insert(te);	
		}
		else
		{
			throw new NoSuchElementException("No Trie exists for Dataset : " + attribute);
		}
	}

	public TrieStatistics getStatistics()
	{
		return getStatistics(DEFAULT_ATTRIBUTE); 
	}

	public TrieStatistics getStatistics(String attribute)
	{
		return attributeNameTrieMap.get(DEFAULT_ATTRIBUTE).getStatistics(); 
	}

	public synchronized void start() throws Exception
	{
		long indexingStartTime = System.nanoTime();
		if (started)
		{
			throw new IllegalStateException("TrieAngulator has already been started.");
		}

		Map<String, Future<Boolean>> trieIndexingResultFuturesMap = new HashMap<>(noOfAttributes);
		for (Trie trie : tries)
		{
			trieIndexingResultFuturesMap.put(trie.getName(), executor.submit(() -> {
				return trie.indexIt();
			}));
		}
		
		for (Map.Entry<String, Future<Boolean>> trieNameIndexResultFutureEntry : trieIndexingResultFuturesMap.entrySet())
		{
			String trieName = trieNameIndexResultFutureEntry.getKey();
			Future<Boolean> indexResultF = trieNameIndexResultFutureEntry.getValue();
			try
			{
				indexResultF.get(trieConfig.getIndexingTimeOutInSeconds(), TimeUnit.SECONDS);
			}
			catch (Exception e)
			{
				throw new Exception("Indexing of Trie: " + trieName + " took longer than " + trieConfig.getIndexingTimeOutInSeconds() + " Seconds.");
			}			
		}
		
		started = true;
	}
	
	public TrieAngulationResult trieangulate(String queryString)
	{
		long trieAngulateStartTime = System.nanoTime();
		
		TrieAngulationResult trieAngulationResult = null; 
		if (!started)
		{
			throw new RuntimeException("Triangulator needs to be started before <trieangulate> can be called.");
		}
		
		if (noOfAttributes == 1)
		{
			trieAngulationResult = trieangulateSerial(trieAngulateStartTime, queryString);
		}
		else
		{
			trieAngulationResult = trieangulateParallel(trieAngulateStartTime, queryString);
		}
		
		return trieAngulationResult;
	}
	
	public void stop()
	{
		executor.shutdown();
	}

	private TrieAngulationResult trieangulateSerial(long trieAngulateStartTime, String queryString)
	{
		List<TraverseResult> traversResultList = new ArrayList<>(noOfAttributes);
		for (Trie trie : tries)
		{
			traversResultList.add(trie.traverse(queryString));
		}
		return createTrieAngulationResult(queryString, trieAngulateStartTime, null, traversResultList);
	}

	private TrieAngulationResult trieangulateParallel(long trieAngulateStartTime, String queryString)
	{
		Map<String, Future<TraverseResult>> traverseResultFuturesMap = new HashMap<>(noOfAttributes);
		for (Trie trie : tries)
		{
			traverseResultFuturesMap.put(trie.getName(), executor.submit(() -> {
				return trie.traverse(queryString);
			}));
		}

		Map<String, Exception> failedTries = new HashMap<>();
		TraverseResult traverseResult = null;
		List<TraverseResult> traversResultList = new ArrayList<>(noOfAttributes);
		for (Map.Entry<String, Future<TraverseResult>> trieTraverseResultFutureEntry : traverseResultFuturesMap.entrySet())
		{
			String trieName = trieTraverseResultFutureEntry.getKey();
			Future<TraverseResult> traverseResultF = trieTraverseResultFutureEntry.getValue();
			try
			{
				traverseResult = traverseResultF.get(trieConfig.getTraverseTimeOutInMilliSeconds(), TimeUnit.MILLISECONDS);
				traversResultList.add(traverseResult);
			}
			catch(Exception e)
			{
				failedTries.put(trieName, e);
			}
		}
		
		return createTrieAngulationResult(queryString, trieAngulateStartTime, failedTries, traversResultList);
	}
	
	private TrieAngulationResult createTrieAngulationResult(String queryString, long trieAngulateStartTime, Map<String, Exception> failedTries, List<TraverseResult> traversResultList)
	{
		TrieAngulationResult trieAngulationResult = null;
		
		if (traversResultList.size() == 1)
		{
			long endTime = System.nanoTime() - trieAngulateStartTime;
			trieAngulationResult = new TrieAngulationResult(queryString, failedTries, traversResultList.get(0).getSuggestions(), endTime); 
		}

		return trieAngulationResult;
	}
}
