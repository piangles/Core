package org.piangles.core.structures;

public final class Trie
{
	private TrieNode root = null;
	private boolean indexed = false;
	private StringArray universeOfWords = null; // TODO Need to address compostie objects
	private SuggestionEngine suggestionEngine = null;

	public Trie(int size)
	{
		root = new TrieNode();
		universeOfWords = new StringArray(size);
	}

	public void insert(String word)
	{
		if (indexed)
		{
			throw new IllegalStateException("Trie is immutable once it has been indexed.");
		}
		universeOfWords.add(word);
	}

	public synchronized void indexIt()
	{
		if (indexed)
		{
			throw new IllegalStateException("Trie has already been indexed.");
		}
		indexed = true;
		universeOfWords.trimToSize();
		universeOfWords.sort();
		suggestionEngine = new SuggestionEngine(universeOfWords);
		
		//TODO Parallel stream this
		String word = null;
		for (int i = 0; i < universeOfWords.size(); ++i)
		{
			word = universeOfWords.get(i).toLowerCase();
			TrieNode current = root;
			for (char ch : word.toCharArray())
			{
				if (Vocabulary.exists(ch))
				{
					current = current.getOrElseCreate(ch);
					current.addIndexIntoOurUniverse(i);
				}
			}
			current.markAsCompleteWord();
		}
		long startTime = System.currentTimeMillis();
		root.indexIt();
		System.out.println("Time Taken to Index nodes: " + (System.currentTimeMillis() - startTime) + " MiliSeconds.");
	}

	public SearchResults search(String word)
	{
		SearchResults searchResults = null;

		word = word.toLowerCase();

		char[] wordAsArray = word.toCharArray();

		boolean useRecursiveAlgorithm = true;
		TraverseResult traverseResult = null;
		if (useRecursiveAlgorithm)
		{
			TrieNode firstNode = root.get(wordAsArray[0]);
			if (firstNode == null)// There is nothing in our universe that
									// starts with this character
			{
				traverseResult = new TraverseResult();
			}
			else
			{
				traverseResult = traverse(firstNode, wordAsArray, 0);
			}
		}
		else
		{
			traverseResult = traverseLoop(root, wordAsArray, 0);
		}

		if (traverseResult.noneFoundInOurUniverse())
		{
			searchResults = new SearchResults(MatchQuality.None, false, false, suggestionEngine.suggestTopTen());
		}
		else
		{
			searchResults = new SearchResults(traverseResult.getMatchQuality(), traverseResult.isPrefix(), traverseResult.isCompleteWord(),
					suggestionEngine.suggest(traverseResult.getIndexesIntoOurUniverse()));
		}
		//System.out.println("Search result for [" + word + "] : " + searchResults);
		return searchResults;
	}

	public boolean isEmpty()
	{
		return root.isEmpty();
	}

	private TraverseResult traverse(TrieNode currentNode, char[] word, int index)
	{
		TraverseResult result = null;

		if (word.length == index + 1)
		{
			/**
			 * We reached the end of the word. We might or might not have more
			 * nodes in this branch. But for certain we have words that being
			 * with this search word.
			 * 
			 * Ex: Search word is 3 and we have 369.
			 * 
			 * Here hit is defined by if the current node is a complete word or
			 * not.
			 */
			MatchQuality matchQuality = currentNode.isCompleteWord()? MatchQuality.Exact : MatchQuality.Partial;
			result = new TraverseResult(matchQuality, currentNode.getIndexesIntoOurUniverse(), currentNode.haveAnyChildren(), currentNode.isCompleteWord());
		}
		else// we continue traversal
		{
			TrieNode childNode = currentNode.get(word[index + 1]);
			if (childNode != null)
			{
				result = traverse(childNode, word, index + 1);
			}
			else
			{
				System.out.println("WHEN DOES IT COME HERE????????????????");
				/**
				 * The search word's next character is not present in out list.
				 * Ex: Search word is cartz and we have cart, carton and
				 * cartoon. Post carT(currentNode) we do not have anyword
				 * starting with Z.
				 */
				result = new TraverseResult(MatchQuality.None, currentNode.getIndexesIntoOurUniverse(), false, false);
			}
		}

		return result;
	}

	private TraverseResult traverseLoop(TrieNode currentNode, char[] word, int index)
	{
		for (int i = 0; i < word.length; i++)
		{
			char ch = word[i];
			TrieNode childNode = currentNode.get(ch);
			if (childNode == null)
			{
				return new TraverseResult(MatchQuality.None, currentNode.getIndexesIntoOurUniverse(), false, false);
			}
			currentNode = childNode;
		}
		MatchQuality matchQuality = currentNode.isCompleteWord()? MatchQuality.Exact : MatchQuality.Partial;
		return new TraverseResult(matchQuality, currentNode.getIndexesIntoOurUniverse(), currentNode.haveAnyChildren(), currentNode.isCompleteWord());
	}
}
