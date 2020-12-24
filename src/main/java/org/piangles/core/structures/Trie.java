package org.piangles.core.structures;

public final class Trie
{
	private TrieNode root = null;
	private boolean indexed = false;
	private StringArray universeOfWords = null;
	private SuggestionEngine suggestionEngine = null;

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
		suggestionEngine = new SuggestionEngine(universeOfWords);
		String word = null;
		for (int i=0; i < universeOfWords.size(); ++i)
		{
			word = universeOfWords.get(i).toLowerCase();
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
	
	public SearchResults search(String word)
	{
		SearchResults searchResults = null;
		
		word = word.toLowerCase();
		TraverseResult traverseResult = traverse(root, word.toCharArray(), -1);
		if (traverseResult.noneFoundInOurUniverse())
		{
			searchResults = new SearchResults(false, false, false, suggestionEngine.suggestTopTen());	
		}
		else
		{
			searchResults = new SearchResults(traverseResult.isHit(), 
					traverseResult.isPrefix(), 
					traverseResult.isCompleteWord(), 
					suggestionEngine.suggest(traverseResult.getIndexIntoUniverse()));
		}
		//System.out.println("Search result for [" + word + "] : " + searchResults);
		return searchResults;
	}
	
	public boolean isEmpty()
	{
		return root.isEmpty();
	}
	
	//Check if the complete word is found or partial match and we reached the end of the search string => index = word.lenght();
	private TraverseResult traverse(TrieNode currentNode, char[] word, int index)
	{
		TraverseResult result = null;
		
		TrieNode nextNode = currentNode.get(word[index+1]);
		if (nextNode == null && index == 0)//There is nothing in the universe that starts with this
		{
			result = new TraverseResult();
		}
		else if (nextNode == null)//We are in the chain and there is no match for the next character
		{
			result = new TraverseResult(false, currentNode.getIndexIntoUniverse(), false, currentNode.isCompleteWord());
		}
		else if (word.length == index+2) //We reached the end of the word, however we have a nextNode => we have longer words which start with this word
		{
			//We had a hit and there is a next node so it is a prefix 
			result = new TraverseResult(true, currentNode.getIndexIntoUniverse(), true, currentNode.isCompleteWord());
		}
		else//we are in the middle of traversal
		{
			result = traverse(nextNode, word, ++index);
		}
		
		return result;
	}
}

//
//TrieNode current = root;
//
//for (int i = 0; i < word.length(); i++)
//{
//	char ch = word.charAt(i);
//	TrieNode node = current.get(ch);
//	if (node == null)
//	{
//		System.out.println("Search result for [" + word + "] found is : false");
//		return false;
//	}
//	current = node;
//}
//System.out.println("Search result for [" + word + "] found is : " + current.isCompleteWord() + " : Actual : " + universeOfWords.elementAt(current.getIndexIntoUniverse()));
//return current.isCompleteWord();
