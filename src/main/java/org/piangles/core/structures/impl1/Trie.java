package org.piangles.core.structures.impl1;

public final class Trie
{
	private TrieNode root;

	public Trie()
	{
		root = new TrieNode();
	}

	public void insert(String word)
	{
		TrieNode current = root;

		for (char l : word.toCharArray())
		{
			current = current.getChildren().computeIfAbsent(l, c -> new TrieNode());
		}
		current.setEndOfWord(true);
	}

	public boolean delete(String word)
	{
		return delete(root, word, 0);
	}

	public boolean containsNode(String word)
	{
		TrieNode current = root;

		for (int i = 0; i < word.length(); i++)
		{
			char ch = word.charAt(i);
			TrieNode node = current.getChildren().get(ch);
			if (node == null)
			{
				//System.out.println("Search result for [" + word + "] found is : false");
				return false;
			}
			current = node;
		}
		//System.out.println("Search result for [" + word + "] found is : " + current.isEndOfWord());
		return current.isEndOfWord();
	}

	public boolean isEmpty()
	{
		return root == null;
	}

	private boolean delete(TrieNode current, String word, int index)
	{
		if (index == word.length())
		{
			if (!current.isEndOfWord())
			{
				return false;
			}
			current.setEndOfWord(false);
			return current.getChildren().isEmpty();
		}
		char ch = word.charAt(index);
		TrieNode node = current.getChildren().get(ch);
		if (node == null)
		{
			return false;
		}
		boolean shouldDeleteCurrentNode = delete(node, word, index + 1) && !node.isEndOfWord();

		if (shouldDeleteCurrentNode)
		{
			current.getChildren().remove(ch);
			return current.getChildren().isEmpty();
		}
		return false;
	}
}