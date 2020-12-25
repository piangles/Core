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
	private int indexIntoUniverse = -1;

	TraverseResult()
	{
		this(false, -1, false, false);
	}
	
	TraverseResult(boolean found, int indexIntoUniverse, boolean prefix, boolean completeWord)
	{
		this.found = found;
		this.indexIntoUniverse = indexIntoUniverse;
		this.prefix = prefix;
		this.completeWord = completeWord;
	}
	
	boolean wasFound()
	{
		return found;
	}
	
	int getIndexIntoUniverse()
	{
		return indexIntoUniverse;
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
		return indexIntoUniverse == -1;
	}
}
