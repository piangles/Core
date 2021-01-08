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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class TrieAngulationResult implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String queryString;
	private Map<String, Exception> failedTries;
	private List<Suggestion> suggestions;
	private long timeTakenInNanoSeconds;
	
	public TrieAngulationResult(String queryString, Map<String, Exception> failedTries)
	{
		this.queryString = queryString;
		this.failedTries = failedTries;
		suggestions = new ArrayList<Suggestion>(); 
	}
	
	public TrieAngulationResult(String queryString, Map<String, Exception> failedTries, List<Suggestion> suggestions, long timeTakenInNanoSeconds)
	{
		this.queryString = queryString;
		this.failedTries = failedTries;
		this.suggestions = suggestions;
		this.timeTakenInNanoSeconds = timeTakenInNanoSeconds;
	}
	
	void addSuggestions(Suggestion[] suggestions)
	{
		this.suggestions.addAll(Arrays.asList(suggestions));
	}
	
	void setTimeTakenInNanoSeconds(long timeTakenInNanoSeconds)
	{
		this.timeTakenInNanoSeconds = timeTakenInNanoSeconds;
	}
	
	public String getQueryString()
	{
		return queryString;
	}
	
	public Map<String, Exception> getFailedTries()
	{
		return failedTries;
	}
	
	public List<Suggestion> getSuggestions()
	{
		return suggestions;
	}

	public long getTimeTakenInNanoSeconds()
	{
		return timeTakenInNanoSeconds;
	}
	
	@Override
	public String toString()
	{
		return "SearchResults [timeTakenInNanoSeconds=" + timeTakenInNanoSeconds + ", suggestions=" + suggestions + "]";
	}
}
