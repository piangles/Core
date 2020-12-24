package org.piangles.core.structures;

import java.util.Arrays;

public final class SearchResults
{
	private boolean hit; //=> we found something or not at all
	private boolean prefix;
	private boolean completeWord;
	private String[] suggestions;
	
	public SearchResults(boolean hit, boolean prefix, boolean completeWord, String[] suggestions)
	{
		this.hit = hit;
		this.prefix = prefix;
		this.completeWord = completeWord;
		this.suggestions = suggestions;
	}

	public boolean isHit()
	{
		return hit;
	}

	public boolean isPrefix()
	{
		return prefix;
	}
	
	public boolean isCompleteWord()
	{
		return completeWord;
	}
	
	public String[] getSuggestions()
	{
		return suggestions;
	}

	@Override
	public String toString()
	{
		return "SearchResults [hit=" + hit + ", prefix=" + prefix + ", completeWord=" + completeWord + ", suggestions=" + Arrays.toString(suggestions) + "]";
	}
}
