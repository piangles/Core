package org.piangles.core.structures;

import java.util.Arrays;
import static org.piangles.core.structures.TrieConstants.SUGGESTIONS_LIMIT;;

final class TrieNode
{
	private char ch;
	private int[] indexesIntoOurUniverse;
	private boolean recycled = false;
	private int indexesCount = 0;
	
	private TrieNode[] children = null; //TODO Need to start it with a small count
	private long childrenBitmap = Vocabulary.NULL;
	
	private boolean completeWord = false;

	TrieNode()
	{
	}

	private TrieNode(char ch)
	{
		this.ch = ch;
		indexesIntoOurUniverse = new int[SUGGESTIONS_LIMIT];
		Arrays.fill(indexesIntoOurUniverse, -1);
	}

	boolean isEmpty()
	{
		return Vocabulary.NULL == childrenBitmap;
	}

	void indexIt()
	{
		/**
		 * Eliminate all indexesIntoOurUniverse which are -1;
		 */
		if (!recycled) //There are some negative indexes into our universe
		{
			if (indexesCount != 0)
			{
				indexesIntoOurUniverse = Arrays.copyOf(indexesIntoOurUniverse, indexesCount-1);
			}
			else
			{
				indexesIntoOurUniverse = null;
			}
		}
		
		if (children != null)
		{
			for (int i=0; i < children.length; ++i)
			{
				children[i].indexIt();
			}
		}
	}
	
	TrieNode get(char ch)
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

	TrieNode getOrElseCreate(char ch)
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
			childrenBitmap = childrenBitmap | Vocabulary.getBinaryRepresentation(ch);
		}
		
		return child;
	}
	
	void addIndexIntoOurUniverse(int indexIntoOurUniverse)
	{
		indexesCount = indexesCount + 1;
		if (indexesCount > SUGGESTIONS_LIMIT)
		{
			indexesCount = 1;
			recycled = true;
		}
		
		indexesIntoOurUniverse[indexesCount-1] = indexIntoOurUniverse;
	}

	boolean doesChildExist(char ch)
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
	
	boolean haveAnyChildren()
	{
		return childrenBitmap == Vocabulary.NULL;
	}
	
	int[] getIndexesIntoOurUniverse()
	{
		return indexesIntoOurUniverse;
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