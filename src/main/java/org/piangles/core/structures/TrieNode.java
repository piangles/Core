package org.piangles.core.structures;

public final class TrieNode
{
	private char ch;
	private int indexIntoUniverse;
	private int[] indexesIntoUniverse;
	
	private TrieNode[] children = null;
	private long childrenBitmap = Vocabulary.NULL;
	
	private boolean completeWord = false;

	TrieNode()
	{
	}

	private TrieNode(char ch, int indexIntoUniverse)
	{
		this.ch = ch;
		this.indexIntoUniverse = indexIntoUniverse;
	}

	public boolean isEmpty()
	{
		return Vocabulary.NULL == childrenBitmap;
	}

	public TrieNode get(char ch)
	{
		TrieNode child = null;
		if (doesChildExist(ch))
		{
			for (int i=0; i < children.length; ++i)
			{
				if (children[i].ch == ch)
				{
					child = children[i];
					break;
				}
			}
		}
		return child;
	}

	public TrieNode getOrElseCreate(char ch, int indexIntoUniverse)
	{
		TrieNode child = get(ch);
		if (child == null)
		{
			child = new TrieNode(ch, indexIntoUniverse); 

			if (children == null)
			{
				children = new TrieNode[1];
			}
			else
			{
				TrieNode[] newChildren = new TrieNode[children.length+1];
				System.arraycopy(children, 0, newChildren, 0, children.length);
				children = newChildren;
			}
			children[children.length - 1] = child;
			childrenBitmap = childrenBitmap | Vocabulary.getBinaryRepresentation(ch);
		}
		
		return child;
	}
	
	public void addIndexIntoUniverse(int indexIntoUniverse)
	{
		
	}

	public boolean doesChildExist(char ch)
	{
		//Need to check whether the bit at given position is set or unset.
		long charBitPosition = Vocabulary.getIndex(ch) + 1;

		//Shift the charBitPosition to the 1st(right most) position
		long shiftedChildren = childrenBitmap >> (charBitPosition - 1);

		/**
		 * Since, right most position is now charBitPosition, AND with 1 will give result.
		 */
		return (shiftedChildren & 1) == 1;
	}
	
	public boolean haveAnyChildren()
	{
		return childrenBitmap == Vocabulary.NULL;
	}
	
	public int getIndexIntoUniverse()
	{
		return indexIntoUniverse;
	}
	//Set specific bit
	//https://stackoverflow.com/questions/4674006/set-specific-bit-in-byte

	boolean isCompleteWord()
	{
		return completeWord;
	}

	void markAsCompleteWord()
	{
		completeWord = true;
	}
}