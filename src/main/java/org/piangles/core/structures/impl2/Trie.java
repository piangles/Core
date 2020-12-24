package org.piangles.core.structures.impl2;

public final class Trie
{
	private TrieNode root = null;

	public Trie()
	{
		root = new TrieNode(); 
	}

	public boolean isEmpty()
	{
		return root.isEmpty();
	}
	
	public void insert(String word)
	{
		TrieNode current = root;

		for (char ch : word.toCharArray())
		{
			current = current.getOrElseCreate(ch);
		}
		current.markAsCompleteWord();
	}
	
	public boolean contains(String word)
	{
		TrieNode current = root;

		for (int i = 0; i < word.length(); i++)
		{
			char ch = word.charAt(i);
			TrieNode node = current.get(ch);
			if (node == null)
			{
				System.out.println("Search result for [" + word + "] found is : false");
				return false;
			}
			current = node;
		}
		System.out.println("Search result for [" + word + "] found is : " + current.isCompleteWord());
		return current.isCompleteWord();
	}
}
