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

import java.util.Arrays;

public final class SearchResults
{
	private String searchedString;
	private long timeTakenInNanoSeconds;
	private int totalSuggestionsAvailable;
	
	private MatchQuality matchQuality;
	private boolean prefix;
	private boolean completeWord;
	private Suggestion[] suggestions;
	
	public SearchResults(String searchedString, long timeTakenInMilliSeconds, MatchQuality matchQuality, boolean prefix, boolean completeWord, 
						int totalSuggestionsAvailable, Suggestion[] suggestions)
	{
		this.searchedString = searchedString;
		this.timeTakenInNanoSeconds = timeTakenInMilliSeconds;
		this.totalSuggestionsAvailable = totalSuggestionsAvailable;
		
		this.matchQuality = matchQuality;
		this.prefix = prefix;
		this.completeWord = completeWord;
		this.suggestions = suggestions;
	}
	
	public String getSearchedString()
	{
		return searchedString;
	}
	
	public long getTimeTakenInNanoSeconds()
	{
		return timeTakenInNanoSeconds;
	}
	
	public int getTotalSuggestionsAvailable()
	{
		return totalSuggestionsAvailable;
	}

	public MatchQuality getMatchQuality()
	{
		return matchQuality;
	}

	public boolean isPrefix()
	{
		return prefix;
	}
	
	public boolean isCompleteWord()
	{
		return completeWord;
	}
	
	public Suggestion[] getSuggestions()
	{
		return suggestions;
	}

	@Override
	public String toString()
	{
		return "SearchResults [timeTakenInNanoSeconds=" + timeTakenInNanoSeconds + ", totalSuggestionsAvailable=" + totalSuggestionsAvailable + ", matchQuality=" + matchQuality
				+ ", prefix=" + prefix + ", completeWord=" + completeWord + ", suggestions=" + Arrays.toString(suggestions) + "]";
	}
}
