package org.piangles.core.structures;

import java.util.Arrays;

public final class SearchResults
{
	private boolean found; //=> Datastore had an exact match of the word
	private boolean prefix;
	private boolean completeWord;
	private String[] suggestions;
	
	public SearchResults(boolean found, boolean prefix, boolean completeWord, String[] suggestions)
	{
		this.found = found;
		this.prefix = prefix;
		this.completeWord = completeWord;
		this.suggestions = suggestions;
	}

	public boolean wasFound()
	{
		return found;
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
		return "SearchResults [found=" + found + ", prefix=" + prefix + ", completeWord=" + completeWord + ", suggestions=" + Arrays.toString(suggestions) + "]";
	}
}
