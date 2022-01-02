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

/**
 * Configurable elements of Trie are
 * 1. maximumWordLength: This controls the depth of the TreeNodes. So longer the word lenght the deeper the nodes will be.
 * 2. suggestionsLimit: This controls the time it takes for search to return, the higher the count more results but more time as well.
 * 3. indexingTimeOutInSeconds: Timeout while indexing.
 * 4. traverseTimeOutInMilliSeconds: Search should yield results in a deterministic manner and this controls it.
 * 5. Traversal approach: Either looping or recursive. 
 *
 */
public final class TrieConfig implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_INITIAL_SIZE = 1000000;
	
	public static final int DEFAULT_MAX_WORD_LENGTH = 20;
	public static final int DEFAULT_SUGGESTIONS_LIMIT = 10;
	public static final int DEFAULT_INDEXING_TIMEOUT_IN_SECONDS = 10;
	public static final int DEFAULT_TRAVERSING_TIMEOUT_IN_MILLISECONDS = 5;
	public static final boolean DEFAULT_USE_RECURSIVE_TRAVERSE_ALGORITHM = true;

	private int maximumWordLength;
	private int suggestionsLimit;
	
	private int indexingTimeOutInSeconds;
	private long traverseTimeOutInMilliSeconds;
	
	private boolean recursiveTraverseAlgorithm;
	
	private TrieEntryList trieEntryList; 
	
	private Vocabulary vocabulary;
	
	private SuggestionEngine suggestionEngine;

	public TrieConfig()
	{
		this(	DEFAULT_MAX_WORD_LENGTH, 
				DEFAULT_SUGGESTIONS_LIMIT, 
				DEFAULT_INDEXING_TIMEOUT_IN_SECONDS,
				DEFAULT_TRAVERSING_TIMEOUT_IN_MILLISECONDS,
				DEFAULT_USE_RECURSIVE_TRAVERSE_ALGORITHM,
				new InMemoryTrieEntryList(DEFAULT_INITIAL_SIZE),
				new DefaultVocabulary(),
				new DefaultSuggestionEngine());
	}
	
	public TrieConfig(	int maximumWordLength, int suggestionsLimit, int indexingTimeOutInSeconds, 
						long traverseTimeOutInMilliSeconds, boolean recursiveAlgorithm, 
						TrieEntryList trieEntryList, Vocabulary vocabulary, SuggestionEngine suggestionEngine)
	{
		this.maximumWordLength = maximumWordLength;
		this.suggestionsLimit = suggestionsLimit;
		this.indexingTimeOutInSeconds = indexingTimeOutInSeconds;
		this.traverseTimeOutInMilliSeconds = traverseTimeOutInMilliSeconds;
		this.recursiveTraverseAlgorithm = recursiveAlgorithm;
		
		this.trieEntryList = trieEntryList;
		this.vocabulary = vocabulary;
		this.suggestionEngine = suggestionEngine;
	}
	
	public TrieConfig setMaximumWordLength(int maximumWordLength)
	{
		this.maximumWordLength = maximumWordLength;
		return this;
	}

	public TrieConfig setSuggestionsLimit(int suggestionsLimit)
	{
		this.suggestionsLimit = suggestionsLimit;
		return this;
	}

	public TrieConfig setIndexingTimeOutInSeconds(int indexingTimeOutInSeconds)
	{
		this.indexingTimeOutInSeconds = indexingTimeOutInSeconds;
		return this;
	}

	public TrieConfig setTraverseTimeOutInMilliSeconds(int traverseTimeOutInMilliSeconds)
	{
		this.traverseTimeOutInMilliSeconds = traverseTimeOutInMilliSeconds;
		return this;
	}

	public TrieConfig setRecursiveAlgorithm(boolean recursiveAlgorithm)
	{
		this.recursiveTraverseAlgorithm = recursiveAlgorithm;
		return this;
	}
	
	public TrieConfig setTrieEntryList(TrieEntryList trieEntryList)
	{
		this.trieEntryList = trieEntryList;
		return this;
	}

	public TrieConfig setVocabulary(Vocabulary vocabulary)
	{
		this.vocabulary = vocabulary;
		return this;
	}

	public int getMaximumWordLength()
	{
		return maximumWordLength;
	}
	
	public int getSuggestionsLimit()
	{
		return suggestionsLimit;
	}

	public int getIndexingTimeOutInSeconds()
	{
		return indexingTimeOutInSeconds;
	}
	
	public long getTraverseTimeOutInMilliSeconds()
	{
		return traverseTimeOutInMilliSeconds;
	}

	public boolean useRecursiveTraverseAlgorithm()
	{
		return recursiveTraverseAlgorithm;
	}
	
	public TrieEntryList getTrieEntryList()
	{
		return trieEntryList;
	}
	
	public Vocabulary getVocabulary()
	{
		return vocabulary;
	}
	
	public SuggestionEngine getSuggestionEngine()
	{
		return suggestionEngine;
	}

	@Override
	public String toString()
	{
		return "TrieConfig [maximumWordLength=" + maximumWordLength + ", suggestionsLimit=" + suggestionsLimit + ", indexingTimeOutInSeconds="
				+ indexingTimeOutInSeconds + ", traverseTimeOutInMilliSeconds=" + traverseTimeOutInMilliSeconds + ", recursiveTraverseAlgorithm=" 
				+ recursiveTraverseAlgorithm + ", trieEntryList=" + trieEntryList.getClass().getSimpleName() + ", vocabulary=" 
				+ vocabulary.getClass().getSimpleName() + ", suggestionEngine=" + suggestionEngine.getClass().getSimpleName() + "]";
	}
}
