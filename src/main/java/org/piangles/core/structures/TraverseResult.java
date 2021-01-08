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

public final class TraverseResult implements Comparable<TraverseResult>
{
	private String trieName = null;
	private String queryString = null;
	private int totalSuggestionsAvailable;
	private MatchQuality matchQuality = null;
	/**
	 * Prefix and Complete word are not complimentary. They both operate
	 * independentaly. Cart is a completeword and also a prefix for Carton and
	 * Cartoon.
	 */
	private boolean prefix;
	private boolean completeWord;
	private int[] indexesIntoTrieEntryList = null;
	private Suggestion[] suggestions;
	private long timeTakenInNanoSeconds;

	TraverseResult(String trieName, String queryString)
	{
		this(trieName, queryString, MatchQuality.None, 0, null, false, false);
	}

	TraverseResult(String trieName, String queryString, MatchQuality matchQuality, int totalSuggestionsAvailable, int[] indexesIntoTrieEntryList, boolean prefix, boolean completeWord)
	{
		this.trieName = trieName;
		this.queryString = queryString;
		this.matchQuality = matchQuality;
		this.totalSuggestionsAvailable = totalSuggestionsAvailable;
		this.indexesIntoTrieEntryList = indexesIntoTrieEntryList;
		this.prefix = prefix;
		this.completeWord = completeWord;
	}
	
	public String getTrieName()
	{
		return trieName;
	}

	public String getQueryString()
	{
		return queryString;
	}

	public void setTimeTakenInNanoSeconds(long timeTakenInNanoSeconds)
	{
		this.timeTakenInNanoSeconds = timeTakenInNanoSeconds;
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

	public int getTotalSuggestionsAvailable()
	{
		return totalSuggestionsAvailable;
	}

	public Suggestion[] getSuggestions()
	{
		return suggestions;
	}

	public long getTimeTakenInNanoSeconds()
	{
		return timeTakenInNanoSeconds;
	}

	@Override
	public int compareTo(TraverseResult other)
	{
		int result = 0;

		if (this.matchQuality == MatchQuality.Exact && other.matchQuality == MatchQuality.Exact)
		{
			if (this.completeWord == other.completeWord)
			{
				result = compareDeep(other);
			}
			else if (this.completeWord)
			{
				result = 1;
			}
			else if (other.completeWord)
			{
				result = -1;
			}
		}
		else if (this.matchQuality == MatchQuality.Exact)
		{
			result = 1;
		}
		else if (other.matchQuality == MatchQuality.Exact)
		{
			result = -1;
		}
		else if (this.matchQuality == MatchQuality.Partial && other.matchQuality == MatchQuality.Partial) 
		{
			if (this.prefix == other.prefix)
			{
				result = compareDeep(other);
			}
			else if (this.prefix)
			{
				result = 1;
			}
			else if (other.prefix)
			{
				result = -1;
			}
		}
		else if (this.matchQuality == MatchQuality.Partial)
		{
			result = 1;
		}
		else //it implies (other.matchQuality == MatchQuality.Partial)
		{
			result = -1;
		}

		return result;
	}

	void setSuggestions(Suggestion[] suggestions)
	{
		this.suggestions = suggestions;
	}

	int[] getIndexesIntoTrieEntryList()
	{
		return indexesIntoTrieEntryList;
	}

	/**
	 * Should not use MatchQuality.None for this method.
	 * indexesIntoTrieEntryList being null reflects accurately purpose of this
	 * method.
	 */
	boolean noneFoundInTrieEntryList()
	{
		return indexesIntoTrieEntryList == null;
	}

	private int compareDeep(TraverseResult other)
	{
		int result = 0;
		Integer thisCount = null;
		Integer otherCount = null;

		thisCount = this.getSuggestions().length;
		otherCount = other.getSuggestions().length;
		
		result = thisCount.compareTo(otherCount);
		
		if (result == 0)
		{
			thisCount = this.totalSuggestionsAvailable;
			otherCount = other.totalSuggestionsAvailable;
			result = thisCount.compareTo(otherCount);
		}

		return result;
	}

	@Override
	public String toString()
	{
		return "TraverseResult [timeTakenInNanoSeconds=" + timeTakenInNanoSeconds + ", matchQuality=" + matchQuality + ", prefix=" + prefix + ", completeWord=" + completeWord
				+ ", totalSuggestionsAvailable=" + totalSuggestionsAvailable + ", suggestions=" + Arrays.toString(suggestions) + "]";
	}
}
