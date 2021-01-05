/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.core.structures;

import java.util.Arrays;;

/**
 *  
 *  
 * 	Bitwise Operations
 * 	https://stackoverflow.com/questions/4674006/set-specific-bit-in-byte
 * 	https://en.wikiversity.org/wiki/Advanced_Java/Bitwise_Operators 
 */
final class TrieNode
{
	private TrieConfig trieConfig = null;
	private char ch;
	private int totalIndexesCount;
	private int[] indexesIntoOurUniverse;
	private boolean recycled = false;
	private int indexesCount = 0;
	
	private TrieNode[] children = null; //TODO Need to start it with a small count
	private long childrenBitmap = Vocabulary.NULL;
	
	private boolean completeWord = false;

	TrieNode(TrieConfig trieConfig)
	{
		this.trieConfig = trieConfig;
	}

	private TrieNode(TrieConfig trieConfig, char ch)
	{
		this.trieConfig = trieConfig;
		this.ch = ch;
		indexesIntoOurUniverse = new int[trieConfig.getSuggestionsLimit()];
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
			child = new TrieNode(trieConfig, ch); 

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
			childrenBitmap = childrenBitmap | trieConfig.getVocabulary().getBinaryRepresentation(ch);
		}
		
		return child;
	}
	
	void addIndexIntoOurUniverse(int indexIntoOurUniverse)
	{
		totalIndexesCount++;
		indexesCount = indexesCount + 1;
		if (indexesCount > trieConfig.getSuggestionsLimit())
		{
			indexesCount = 1;
			recycled = true;
		}

		indexesIntoOurUniverse[indexesCount-1] = indexIntoOurUniverse;
	}

	boolean doesChildExist(char ch)
	{
		//Need to check whether the bit at given position is set or unset.
		long charBitPosition = trieConfig.getVocabulary().getIndex(ch) + 1;

		//Shift the charBitPosition to the 1st(right most) position
		long shiftedChildren = childrenBitmap >> (charBitPosition - 1);

		/**
		 * Since, right most position is now charBitPosition, AND with 1 will give result.
		 */
		return (shiftedChildren & 1) == 1;
	}
	
	boolean haveAnyChildren()
	{
		return !(childrenBitmap == Vocabulary.NULL);
	}
	
	int[] getIndexesIntoOurUniverse()
	{
		return indexesIntoOurUniverse;
	}
	
	void removeChild(char ch)
	{
		long charBitPosition = trieConfig.getVocabulary().getIndex(ch) + 1;
		childrenBitmap &= ~(1 << charBitPosition);
	}

	int getTotalIndexesCount()
	{
		return totalIndexesCount;
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
