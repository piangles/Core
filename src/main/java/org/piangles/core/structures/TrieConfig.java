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

public final class TrieConfig
{
	public static final int DEFAULT_INITIAL_SIZE = 1000000;
	public static final int DEFAULT_MAX_WORD_LENGTH = 20;
	public static final int DEFAULT_SUGGESTIONS_LIMIT = 10;
	public static final boolean DEFAULT_USE_RECURSIVE_ALGORITHM = true;
	public static final boolean DEFAULT_PERF_MONITORING = true;

	private int initialSize;
	private int maximumWordLength;
	private int suggestionsLimit;
	
	private boolean recursiveAlgorithm;
	private boolean performanceMonitoringEnabled;

	public TrieConfig()
	{
		this(DEFAULT_INITIAL_SIZE, DEFAULT_MAX_WORD_LENGTH, DEFAULT_SUGGESTIONS_LIMIT, DEFAULT_USE_RECURSIVE_ALGORITHM, DEFAULT_PERF_MONITORING);
	}
	
	public TrieConfig(int initialSize, int maximumWordLength, int suggestionsLimit, boolean recursiveAlgorithm, boolean performanceMonitoringEnabled)
	{
		this.initialSize = initialSize;
		this.maximumWordLength = maximumWordLength;
		this.suggestionsLimit = suggestionsLimit;
		this.recursiveAlgorithm = recursiveAlgorithm;
		this.performanceMonitoringEnabled = performanceMonitoringEnabled;
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
	
	public boolean useRecursiveAlgorithm()
	{
		return recursiveAlgorithm;
	}
	
	public boolean isPerformanceMonitoringEnabled()
	{
		return performanceMonitoringEnabled;
	}
}
