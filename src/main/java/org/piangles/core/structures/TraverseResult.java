package org.piangles.core.structures;

final class TraverseResult
{
	private MatchQuality matchQuality = null;
	/**
	 * Prefix and Complete word are not complimentary. 
	 * They both operate independentaly.
	 * Cart is a completeword and also a prefix for Carton and Cartoon.
	 */
	private boolean prefix;
	private boolean completeWord;
	private int[] indexesIntoOurUniverse = null;

	TraverseResult()
	{
		this(MatchQuality.None, null, false, false);
	}
	
	TraverseResult(MatchQuality matchQuality, int[] indexesIntoOurUniverse, boolean prefix, boolean completeWord)
	{
		this.matchQuality = matchQuality;
		this.indexesIntoOurUniverse = indexesIntoOurUniverse;
		this.prefix = prefix;
		this.completeWord = completeWord;
	}
	
	public MatchQuality getMatchQuality()
	{
		return matchQuality;	
	}
	
	int[] getIndexesIntoOurUniverse()
	{
		return indexesIntoOurUniverse;
	}
	
	boolean isPrefix()
	{
		return prefix;
	}
	
	boolean isCompleteWord()
	{
		return completeWord;
	}
	
	/**
	 * Should not use MatchQuality.None for this method.
	 * indexesIntoOurUniverse being null reflects accurately
	 * purpose of this method.
	 */
	boolean noneFoundInOurUniverse()
	{
		return indexesIntoOurUniverse == null;
	}
}
