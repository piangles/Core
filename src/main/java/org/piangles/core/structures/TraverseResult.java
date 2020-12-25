package org.piangles.core.structures;

final class TraverseResult
{
	private boolean found = false;
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
		this(false, null, false, false);
	}
	
	TraverseResult(boolean found, int[] indexesIntoOurUniverse, boolean prefix, boolean completeWord)
	{
		this.found = found;
		this.indexesIntoOurUniverse = indexesIntoOurUniverse;
		this.prefix = prefix;
		this.completeWord = completeWord;
	}
	
	boolean wasFound()
	{
		return found;
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
	
	boolean noneFoundInOurUniverse()
	{
		return indexesIntoOurUniverse == null;
	}
}
