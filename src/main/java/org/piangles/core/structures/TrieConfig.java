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

public final class TrieConfig implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_INITIAL_SIZE = 1000000;
	public static final int DEFAULT_MAX_WORD_LENGTH = 20;
	public static final int DEFAULT_SUGGESTIONS_LIMIT = 10;
	public static final int DEFAULT_INDEXING_TIMEOUT_IN_SECONDS = 10;
	public static final int DEFAULT_TRAVERSING_TIMEOUT_IN_MILLISECONDS = 5;
	public static final boolean DEFAULT_USE_RECURSIVE_TRAVERSE_ALGORITHM = true;

	private int initialSize;
	private int maximumWordLength;
	private int suggestionsLimit;
	
	private int indexingTimeOutInSeconds;
	private long traverseTimeOutInMilliSeconds;
	
	private boolean recursiveTraverseAlgorithm;
	
	private Vocabulary vocabulary;

	public TrieConfig()
	{
		this(	DEFAULT_INITIAL_SIZE, 
				DEFAULT_MAX_WORD_LENGTH, 
				DEFAULT_SUGGESTIONS_LIMIT, 
				DEFAULT_INDEXING_TIMEOUT_IN_SECONDS,
				DEFAULT_TRAVERSING_TIMEOUT_IN_MILLISECONDS,
				DEFAULT_USE_RECURSIVE_TRAVERSE_ALGORITHM, 
				new DefaultVocabulary());
	}
	
	public TrieConfig(	int initialSize, int maximumWordLength, int suggestionsLimit, int indexingTimeOutInSeconds, 
						long traverseTimeOutInMilliSeconds, boolean recursiveAlgorithm, Vocabulary vocabulary)
	{
		this.initialSize = initialSize;
		this.maximumWordLength = maximumWordLength;
		this.suggestionsLimit = suggestionsLimit;
		this.indexingTimeOutInSeconds = indexingTimeOutInSeconds;
		this.traverseTimeOutInMilliSeconds = traverseTimeOutInMilliSeconds;
		this.recursiveTraverseAlgorithm = recursiveAlgorithm;
		this.vocabulary = vocabulary;
	}
	
	public TrieConfig setInitialSize(int initialSize)
	{
		this.initialSize = initialSize;
		return this;
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

	public TrieConfig setVocabulary(Vocabulary vocabulary)
	{
		this.vocabulary = vocabulary;
		return this;
	}

	public int getInitialSize()
	{
		return initialSize;
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
	
	public Vocabulary getVocabulary()
	{
		return vocabulary;
	}

	@Override
	public String toString()
	{
		return "TrieConfig [initialSize=" + initialSize + ", maximumWordLength=" + maximumWordLength + ", suggestionsLimit=" + suggestionsLimit + ", indexingTimeOutInSeconds="
				+ indexingTimeOutInSeconds + ", traverseTimeOutInMilliSeconds=" + traverseTimeOutInMilliSeconds + ", recursiveTraverseAlgorithm=" + recursiveTraverseAlgorithm + ", vocabulary="
				+ vocabulary.getClass().getSimpleName() + "]";
	}
	
	
}
