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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class TrieAngulator
{
	private static final String DEFAULT_TRIE_NAME = "Default";
	private static final int NO_OF_CORE = Runtime.getRuntime().availableProcessors();
	
    private String datasetName = null;
    private TrieConfig trieConfig = null;
    private TrieAngulatorStatistics trieAngulatorStatistics = null;
    
    private HashMap<String, Trie> trieNameAndTrieMap = null;
	private Collection<Trie> tries = null;
	
	private int noOfTries = 0;
    private ExecutorService executor = null;
	private boolean started = false;
	
	public TrieAngulator(String datasetName, TrieConfig trieConfig)
	{
		this(datasetName, new HashSet<String>(Arrays.asList(new String[]{DEFAULT_TRIE_NAME})), trieConfig);
	}

	//Make it a set
	public TrieAngulator(String datasetName, Set<String> trieNames, TrieConfig trieConfig)
	{
		this.datasetName = datasetName;
		this.trieConfig = trieConfig;

		trieAngulatorStatistics = new TrieAngulatorStatistics(datasetName);
		
		noOfTries = trieNames.size();
		if (noOfTries == 1)
		{
			executor = Executors.newSingleThreadExecutor();
		}
		else
		{
			executor = Executors.newFixedThreadPool(noOfTries);
		}
		trieNameAndTrieMap = new HashMap<>(noOfTries);
		for (String attribute : trieNames)
		{
			trieNameAndTrieMap.put(attribute, new Trie(attribute, trieConfig));
		}
		tries = trieNameAndTrieMap.values();
	}
	
	public String getDatasetName()
	{
		return datasetName;
	}

	public void insert(TrieEntry te)
	{
		trieNameAndTrieMap.get(DEFAULT_TRIE_NAME).insert(te);
	}

	public void insert(String attribute, TrieEntry te)
	{
		Trie trie = trieNameAndTrieMap.get(attribute);
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
		TrieStatistics trieStatistics = null;

		boolean firstTrie = true;
		//trieangulatorStatistics
		for (Trie trie : tries)
		{
			if(firstTrie)
			{
				firstTrie = false;
				try
				{
					trieStatistics = (TrieStatistics)trie.getStatistics().clone();
				}
				catch (CloneNotSupportedException e)
				{
					throw new RuntimeException(e);
				}
			}
			else
			{
				trieStatistics.merge(trie.getStatistics());
			}
		}
		return trieStatistics;
	}

	public synchronized void start() throws Exception
	{
		long indexingStartTime = System.nanoTime();
		if (started)
		{
			throw new IllegalStateException("TrieAngulator has already been started.");
		}

		Map<String, Future<Boolean>> trieIndexingResultFuturesMap = new HashMap<>(noOfTries);
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
		
		if (noOfTries < NO_OF_CORE)
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
		List<TraverseResult> traversResultList = new ArrayList<>(noOfTries);
		for (Trie trie : tries)
		{
			traversResultList.add(trie.traverse(queryString));
		}
		return createTrieAngulationResult(queryString, trieAngulateStartTime, null, traversResultList);
	}

	private TrieAngulationResult trieangulateParallel(long trieAngulateStartTime, String queryString)
	{
		Map<String, Future<TraverseResult>> traverseResultFuturesMap = new HashMap<>(noOfTries);
		for (Trie trie : tries)
		{
			traverseResultFuturesMap.put(trie.getName(), executor.submit(() -> {
				return trie.traverse(queryString);
			}));
		}

		Map<String, Exception> failedTries = new HashMap<>();
		TraverseResult traverseResult = null;
		List<TraverseResult> traverseResultList = new ArrayList<>(noOfTries);
		for (Map.Entry<String, Future<TraverseResult>> trieTraverseResultFutureEntry : traverseResultFuturesMap.entrySet())
		{
			String trieName = trieTraverseResultFutureEntry.getKey();
			Future<TraverseResult> traverseResultF = trieTraverseResultFutureEntry.getValue();
			try
			{
				traverseResult = traverseResultF.get(trieConfig.getTraverseTimeOutInMilliSeconds(), TimeUnit.MILLISECONDS);
				traverseResultList.add(traverseResult);
			}
			catch(Exception e)
			{
				failedTries.put(trieName, e);
			}
		}
		
		return createTrieAngulationResult(queryString, trieAngulateStartTime, failedTries, traverseResultList);
	}
	
	private TrieAngulationResult createTrieAngulationResult(String queryString, long trieAngulateStartTime, Map<String, Exception> failedTries, List<TraverseResult> traversResultList)
	{
		System.out.println("Time Taken before TrieAngulationResult Creation : " + (System.nanoTime() - trieAngulateStartTime));

		TrieAngulationResult trieAngulationResult = null;

		//Eliminate results which are empty
		traversResultList = traversResultList.stream().filter(tr -> !tr.getMatchQuality().equals(MatchQuality.None)).collect(Collectors.toList());
		
		if (traversResultList.size() == 1)
		{
			long endTime = System.nanoTime() - trieAngulateStartTime;
			trieAngulationResult = new TrieAngulationResult(queryString, failedTries, Arrays.asList(traversResultList.get(0).getSuggestions()), endTime); 
		}
		else if (traversResultList.size() > 1)
		{
			trieAngulationResult = new TrieAngulationResult(queryString, failedTries);
			Collections.sort(traversResultList, Collections.reverseOrder());

			int suggestionsLeft = trieConfig.getSuggestionsLimit();
			for (TraverseResult traverseResult : traversResultList)
			{
				//System.out.println("Combining :: " + traverseResult.getTrieName() + " : " + Arrays.toString(traverseResult.getSuggestions()));
				if (suggestionsLeft <= 0)
				{
					break;
				}
				else if (suggestionsLeft > traverseResult.getSuggestions().length)
				{
					trieAngulationResult.addSuggestions(traverseResult.getSuggestions());
				}
				else
				{
					trieAngulationResult.addSuggestions(Arrays.copyOfRange(traverseResult.getSuggestions(), 0, suggestionsLeft));
				}
				suggestionsLeft = suggestionsLeft - traverseResult.getSuggestions().length;
			}
			
			trieAngulationResult.setTimeTakenInNanoSeconds(System.nanoTime() - trieAngulateStartTime);
		}
		else //Empty Results
		{
			long endTime = System.nanoTime() - trieAngulateStartTime;
			trieAngulationResult = new TrieAngulationResult(queryString, failedTries, null, endTime); 
		}

		return trieAngulationResult;
	}
}
