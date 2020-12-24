package org.piangles.core.structures;

public final class Trie
{
	private TrieNode root = null;
	private StringArray universeOfWords = null;

	public Trie(int size)
	{
		root = new TrieNode();
		universeOfWords = new StringArray(size);
	}

	public void insert(String word)
	{
		universeOfWords.add(word);
	}
	
	public void indexIt()
	{
		universeOfWords.sort();

		String word = null;
		for (int i=0; i < universeOfWords.size(); ++i)
		{
			word = universeOfWords.elementAt(i).toLowerCase();
			TrieNode current = root;
			//System.out.println("" + i + ":" + word);

			for (char ch : word.toCharArray())
			{
				if (Vocabulary.exists(ch))
				{
					current = current.getOrElseCreate(ch, i);
				}
			}
			current.markAsCompleteWord();
		}
	}
	
	public boolean contains(String word)
	{
		word = word.toLowerCase();
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
		System.out.println("Search result for [" + word + "] found is : " + current.isCompleteWord() + " : Actual : " + universeOfWords.elementAt(current.getIndexIntoUniverse()));
		return current.isCompleteWord();
	}
	
	public String[] suggestions()
	{
		return null;
	}
	
	public boolean isEmpty()
	{
		return root.isEmpty();
	}
}
