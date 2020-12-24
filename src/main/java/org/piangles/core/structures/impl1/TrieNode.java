package org.piangles.core.structures.impl1;

import java.util.HashMap;
import java.util.Map;

final class TrieNode
{
	private final Map<Character, TrieNode> children = new HashMap<>();
	private boolean endOfWord;

	Map<Character, TrieNode> getChildren()
	{
		return children;
	}

	boolean isEndOfWord()
	{
		return endOfWord;
	}

	void setEndOfWord(boolean endOfWord)
	{
		this.endOfWord = endOfWord;
	}
}