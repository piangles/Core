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
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class TrieAngulator
{
	private static final String DEFAULT_ATTRIBUTE = "Default";
	
    private String datasetName = null;
	
    private HashMap<String, Trie> attributeTrieMap = null;
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
		this.datasetName = datasetName;
		noOfAttributes = attributes.size();
		if (noOfAttributes == 1)
		{
			executor = Executors.newSingleThreadExecutor();
		}
		else
		{
			executor = Executors.newFixedThreadPool(noOfAttributes);
		}
		attributeTrieMap = new HashMap<>(noOfAttributes);
		for (String attribute : attributes)
		{
			attributeTrieMap.put(attribute, new Trie(attribute, trieConfig));
		}
		tries = attributeTrieMap.values();
	}
	
	public String getDatasetName()
	{
		return datasetName;
	}

	public void insert(TrieEntry te)
	{
		attributeTrieMap.get(DEFAULT_ATTRIBUTE).insert(te);
	}

	public void insert(String attribute, TrieEntry te)
	{
		Trie trie = attributeTrieMap.get(attribute);
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
		return attributeTrieMap.get(DEFAULT_ATTRIBUTE).getStatistics(); 
	}

	public synchronized void start() throws Exception
	{
		if (started)
		{
			throw new IllegalStateException("TrieAngulator has already been started.");
		}

		List<Future<Boolean>> indexResultFutures = new ArrayList<>(noOfAttributes);
		for (Trie trie : tries)
		{
			indexResultFutures.add(executor.submit(() -> {
				return trie.indexIt();
			}));
		}
		
		for (Future<Boolean> indexResultFuture : indexResultFutures)
		{
			//TODO which one failed??
			indexResultFuture.get(60, TimeUnit.SECONDS);			
		}
		
		started = true;
	}
	
	public SearchResults search(String searchString) throws Exception
	{
		SearchResults searchResults = null;
		
		List<Future<SearchResults>> searchResultFutures = new ArrayList<>(noOfAttributes);
		for (Trie trie : tries)
		{
			searchResultFutures.add(executor.submit(() -> {
				return trie.search(searchString);
			}));
		}

		SearchResults searchResult = null;
		List<SearchResults> searchResultsList = new ArrayList<>(noOfAttributes);
		for (Future<SearchResults> searchResultFuture : searchResultFutures)
		{
			//TODO which one failed??
			searchResult = searchResultFuture.get(100, TimeUnit.MILLISECONDS);
			//if (searchResult.getMatchQuality() != MatchQuality.None)
			{
				searchResultsList.add(searchResult);
			}
		}
		
		//Stitch/Triangulate SuggestionResult results here.
		return searchResultsList.get(0);
	}
	
	public void stop()
	{
		executor.shutdown();
	}
}
