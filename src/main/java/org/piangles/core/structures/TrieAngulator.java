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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class TrieAngulator
{
	private static final String DEFAULT_CONTEXT = "Default";
	
    private ExecutorService executor = null;
	
	private HashMap<String, Trie> contextTrieMap = null;
	private Collection<Trie> tries = null;
	private int noOfContexts = 0;
	private boolean indexed = false;
	
	 // TODO Need to address compostie objects
	private StringArray universeOfWords = null;
	private List<TrieEntry> entries = null;
	
	public TrieAngulator(TrieConfig trieConfig) //Where do we get context names from?
	{
		this(Arrays.asList(new String[]{DEFAULT_CONTEXT}), trieConfig);
	}

	public TrieAngulator(List<String> contexts, TrieConfig trieConfig) //Where do we get context names from?
	{
		noOfContexts = contexts.size();
		if (noOfContexts == 1)
		{
			executor = Executors.newSingleThreadExecutor();
		}
		else
		{
			executor = Executors.newFixedThreadPool(noOfContexts);
		}
		contextTrieMap = new HashMap<>(noOfContexts);
		for (String context : contexts)
		{
			contextTrieMap.put(context, new Trie(context, trieConfig));
		}
		tries = contextTrieMap.values();
	}

	public void insert(String word)
	{
		contextTrieMap.get(DEFAULT_CONTEXT).insert(word);
	}
	
	public TrieStatistics getStatistics()
	{
		return getStatistics(DEFAULT_CONTEXT); 
	}

	public TrieStatistics getStatistics(String context)
	{
		return contextTrieMap.get(DEFAULT_CONTEXT).getStatistics(); 
	}

	public synchronized void indexIt() throws Exception
	{
		if (indexed)
		{
			throw new IllegalStateException("Trie has already been indexed.");
		}

		List<Future<Boolean>> indexResultFutures = new ArrayList<>(noOfContexts);
		for (Trie trie : tries)
		{
			indexResultFutures.add(executor.submit(() -> {
				return trie.indexIt();
			}));
		}
		
		for (Future<Boolean> indexResultFuture : indexResultFutures)
		{
			//TODO which one failed?
			indexResultFuture.get(10, TimeUnit.SECONDS);			
		}
		
		indexed = true;
	}
	
	public SearchResults search(String searchString) throws Exception
	{
		SearchResults searchResults = null;
		
		List<Future<SearchResults>> searchResultFutures = new ArrayList<>(noOfContexts);
		for (Trie trie : tries)
		{
			searchResultFutures.add(executor.submit(() -> {
				return trie.search(searchString);
			}));
		}
		
//		for (Future<SearchResults> searchResultFuture : searchResultFutures)
//		{
//			//TODO which one failed?
//			searchResultFuture.get(100, TimeUnit.MILLISECONDS);
//		}
		
		//Stich results here.
		return searchResultFutures.get(0).get(100, TimeUnit.MILLISECONDS);
	}
}
