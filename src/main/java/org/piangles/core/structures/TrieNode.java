package org.piangles.core.structures;

public final class TrieNode
{
	private char ch;
	private int indexIntoUniverse;
	private long bitmap = Vocabulary.NULL;
	private TrieNode[] children = null;
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
		return Vocabulary.NULL == bitmap;
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
			bitmap = bitmap | Vocabulary.getBinaryRepresentation(ch);
		}
		
		return child;
	}

	/**
	 * function to check whether the bit at given position is set or unset.
	 * 
	 * @param ch
	 * @return
	 */
	public boolean doesChildExist(char ch)
	{
		long k = Vocabulary.getIndex(ch) + 1;

		// to shift the kth bit at 1st position
		long shiftedChildren = bitmap >> (k - 1);

		/**
		 * Since, last bit is now kth bit, so doing AND with 1 will give result.
		 */
		return (shiftedChildren & 1) == 1;
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