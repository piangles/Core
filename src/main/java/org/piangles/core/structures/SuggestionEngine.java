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
	
	public String[] suggest(int[] indexesIntoOurUniverse)
	{
		String[] suggestions = new String[indexesIntoOurUniverse.length];
		for (int i=0; i < indexesIntoOurUniverse.length; ++i)
		{
			suggestions[i] = universeOfWords.get(indexesIntoOurUniverse[i]);
		}
		return suggestions;
	}
}
