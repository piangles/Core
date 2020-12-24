package org.piangles.core.structures.impl2;

public final class TrieNode
{
	private char ch;
	private long group1Bitmap = Vocabulary.NULL;
	private long group2Bitmap = Vocabulary.NULL;
	private TrieNode[] children = null;
	private boolean completeWord = false;

	public TrieNode()
	{
	}

	private TrieNode(char ch)
	{
		this.ch = ch;
	}

	public boolean isEmpty()
	{
		return Vocabulary.NULL == group1Bitmap && Vocabulary.NULL == group2Bitmap;
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

	public TrieNode getOrElseCreate(char ch)
	{
		TrieNode child = get(ch);
		if (child == null)
		{
			child = new TrieNode(ch); 

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
			if ((int)ch < 123)
			{
				group1Bitmap = group1Bitmap | Vocabulary.getBinaryRepresentation(ch);
			}
			else
			{
				group2Bitmap = group2Bitmap | Vocabulary.getBinaryRepresentation(ch); 
			}
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
		long groupBitmap = ((int)ch < 123)? group1Bitmap : group2Bitmap; 
		long k = Vocabulary.getCharacterDetails(ch).index + 1;

		// to shift the kth bit at 1st position
		long shiftedChildren = groupBitmap >> (k - 1);

		/**
		 * Since, last bit is now kth bit, so doing AND with 1 will give result.
		 */
		return (shiftedChildren & 1) == 1;
	}
	
	boolean isCompleteWord()
	{
		return completeWord;
	}

	void markAsCompleteWord()
	{
		completeWord = true;
	}
}