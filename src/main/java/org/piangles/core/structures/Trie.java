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

public final class Trie
{
	private static final TraverseResult NONE_FOUND = new TraverseResult();
	private TrieConfig trieConfig = null;
	private TrieStatistics trieStatistics = null;
	private TrieNode root = null;
	private boolean indexed = false;
	private StringArray universeOfWords = null; // TODO Need to address compostie objects
	private SuggestionEngine suggestionEngine = null;
	
	private Vocabulary vocabulary = null; 

	public Trie(TrieConfig trieConfig)
	{
		this.trieConfig = trieConfig;
		vocabulary = trieConfig.getVocabulary();
		this.trieStatistics = new TrieStatistics();
		root = new TrieNode(trieConfig);
		universeOfWords = new StringArray(trieConfig.getInitialSize());
	}

	public TrieStatistics getStatistics()
	{
		return trieStatistics; 
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
		long startTime = System.currentTimeMillis();
		universeOfWords.trimToSize();
		universeOfWords.sort();
		System.out.println("Time Taken to sort: " + (System.currentTimeMillis() - startTime) + " MiliSeconds.");

		suggestionEngine = new SuggestionEngine(universeOfWords);
		
		//TODO Parallel stream this
		startTime = System.currentTimeMillis();
		String word = null;
		for (int i = 0; i < universeOfWords.size(); ++i)
		{
			word = universeOfWords.get(i).toLowerCase();
			TrieNode current = root;
			for (char ch : word.toCharArray())
			{
				if (trieConfig.getVocabulary().exists(ch))
				{
					current = current.getOrElseCreate(ch);
					current.addIndexIntoOurUniverse(i);
				}
			}
			current.markAsCompleteWord();
		}
		System.out.println("Time Taken to Index : " + (System.currentTimeMillis() - startTime) + " MiliSeconds.");

		startTime = System.currentTimeMillis();
		root.indexIt();
		if (trieConfig.isPerformanceMonitoringEnabled())
		{
			System.out.println("Time Taken to Index nodes: " + (System.currentTimeMillis() - startTime) + " MiliSeconds.");
		}
	}

	public SearchResults search(String word)
	{
		long startTime = System.nanoTime();
		SearchResults searchResults = null;

		word = word.toLowerCase();

		char[] wordAsArray = word.toCharArray();

		TraverseResult traverseResult = null;
		if (trieConfig.useRecursiveAlgorithm())
		{
			TrieNode firstNode = null;
			
			if (trieConfig.getVocabulary().exists(wordAsArray[0]))
			{
				System.out.println("**************" + wordAsArray[0] + ":" + (System.nanoTime() - startTime));

				firstNode = root.get(wordAsArray[0]);
			}
			if (firstNode != null)
			{
				traverseResult = traverse(firstNode, wordAsArray, 0);
			}
			else
			{
				System.out.println("**************" + (System.nanoTime() - startTime));
				/**
				 * There is nothing in our universe that
				 * starts with this characters
				 */
				traverseResult = NONE_FOUND;
			}
		}
		else
		{
			traverseResult = traverseLoop(root, wordAsArray, 0);
		}
		long timeTaken = System.nanoTime() - startTime;
		
		if (traverseResult.noneFoundInOurUniverse())
		{
			searchResults = new SearchResults(timeTaken, MatchQuality.None, false, false, 0, suggestionEngine.suggestTopTen());
		}
		else
		{
			searchResults = new SearchResults(timeTaken, traverseResult.getMatchQuality(), traverseResult.isPrefix(), traverseResult.isCompleteWord(),
					traverseResult.getTotalSuggestionsAvailable(), suggestionEngine.suggest(traverseResult.getIndexesIntoOurUniverse()));
		}
		if (trieConfig.isPerformanceMonitoringEnabled())
		{
			System.out.println("Search result for [" + word + "] : " + searchResults);
		}
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
			result = new TraverseResult(matchQuality, currentNode.getTotalIndexesCount(), currentNode.getIndexesIntoOurUniverse(), currentNode.haveAnyChildren(), currentNode.isCompleteWord());
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
				 * Ex: Search word is *cartz* and we have 
				 * Scenario 1 : cart 
				 * Scenario 2 : carton and cartoon. Post carT(currentNode) we do 
				 * not have any word starting with Z.
				 */
				result = new TraverseResult(MatchQuality.None, currentNode.getTotalIndexesCount(), currentNode.getIndexesIntoOurUniverse(), false, false);
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
				return new TraverseResult(MatchQuality.None, currentNode.getTotalIndexesCount(), currentNode.getIndexesIntoOurUniverse(), false, false);
			}
			currentNode = childNode;
		}
		MatchQuality matchQuality = currentNode.isCompleteWord()? MatchQuality.Exact : MatchQuality.Partial;
		return new TraverseResult(matchQuality, currentNode.getTotalIndexesCount(), currentNode.getIndexesIntoOurUniverse(), currentNode.haveAnyChildren(), currentNode.isCompleteWord());
	}
}
