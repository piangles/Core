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
//			if (word.startsWith("ana"))
//			System.out.println("" + i + ":" + word);

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

		char[] wordAsArray = word.toCharArray();
		TraverseResult traverseResult = null; 
		TrieNode nextNode = root.get(wordAsArray[0]);
		if (nextNode == null)//There is nothing in our universe that starts with this character
		{
			traverseResult = new TraverseResult();
		}
		else
		{
			traverseResult = traverse(nextNode, wordAsArray, 0);
		}

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
		
		if (word.length == index+1)
		{
			/**
			 * We reached the end of the word. We might or might not have more nodes in this branch.
			 * But for certain we have words that being with this search word. 
			 * 
			 * Ex:  Search word is 3 and we have 369.
			 * 
			 * Here hit is defined by if the current node is a complete word or not.
			 */
			result = new TraverseResult(currentNode.isCompleteWord(), currentNode.getIndexIntoUniverse(), true, currentNode.isCompleteWord());
		}
		else//we continue traversal
		{
			TrieNode nextNode = currentNode.get(word[index+1]);
			if (nextNode != null)
			{
				result = traverse(nextNode, word, index+1);
			}
			else
			{
				/**
				 * The search word's next character is not present in out list.
				 * Ex: Search word is cartz and we have cart, carton and cartoon.
				 * Post carT(currentNode) we do not have anyword starting with Z. 
				 */
				result = new TraverseResult(false, currentNode.getIndexIntoUniverse(), false, false);
			}
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
