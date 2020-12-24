package org.piangles.core.structures;

final class SuggestionEngine
{
	private StringArray universeOfWords = null;
	
	SuggestionEngine(StringArray universeOfWords)
	{
		this.universeOfWords = universeOfWords;
	}
	
	public String[] suggestTopTen()
	{
		return null;
	}
	
	public String[] suggest(int indexIntoUniverse)
	{
		return new String[]{universeOfWords.get(indexIntoUniverse)};
	}
}
