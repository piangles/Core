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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the mother of all TrieNodes, one starts with creation of Trie.
 * Creation of Trie is dirven by 2 parameters.
 * 1. Name of the Trie : A name that describes the Dataset.
 * 2. TrieConfig : That governs the various parameters for Trie to organize it's structure.
 * 
 * This is an highly performant, 
 * Once a Trie is indexed it will remain immutable through it's existence. 
 * So it is important
 * to figure 
 */
public final class Trie implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static final String WARM_UP = "az";
	
	private String name;
	private TrieConfig trieConfig = null;
	private TrieStatistics trieStatistics = null;
	
	private TrieNode root = null;
	private boolean indexed = false;
	
	private Map<String, String> stopWordsMap = null; 
	private TrieEntryList trieEntryList = null;
	
	private SuggestionEngine suggestionEngine = null;
	
	public Trie(String name, TrieConfig trieConfig)
	{
		this.name = name;
		this.trieConfig = trieConfig;
		
		trieStatistics = new TrieStatistics(name);
		
		stopWordsMap = new HashMap<>();
		for (String stopWord : trieConfig.getVocabulary().getStopWords())
		{
			stopWordsMap.put(stopWord, stopWord);
		}
		
		root = new TrieNode(trieStatistics, trieConfig);
		trieEntryList = trieConfig.getTrieEntryList();
	}
	
	public String getName()
	{
		return name;
	}

	public TrieStatistics getStatistics()
	{
		return trieStatistics; 
	}
	
	public void insert(TrieEntry te)
	{
		if (indexed)
		{
			throw new IllegalStateException("Trie is immutable once it has been indexed.");
		}
		trieEntryList.add(te);
		
		if (te.getTransformedValue().indexOf(Vocabulary.TOKEN_DELIMITER) != -1)
		{
			String[] splits = te.getTransformedValue().split("\\s+");
			for (int i=1; i < splits.length; ++i)
			{
				if (!stopWordsMap.containsKey(splits[i]))
				{
					trieEntryList.add(new TrieEntry(te.getId(), te, te.getRank(), splits[i], splits[i]));
					trieStatistics.incrementDerviedDatasetSize();
				}
				else
				{
					trieStatistics.incrementSkippedStopWords();
				}
			}
		}
	}

	public synchronized boolean indexIt()
	{
		if (indexed)
		{
			throw new IllegalStateException("Trie has already been indexed.");
		}
		trieStatistics.start(TrieMetrics.Readiness);
		trieStatistics.start(TrieMetrics.SortDataset);
		trieEntryList.trimToSize();
		trieEntryList.sort();
		trieStatistics.end(TrieMetrics.SortDataset);
		
		suggestionEngine = new SuggestionEngine(name, trieEntryList);
		
		trieStatistics.start(TrieMetrics.PopulateTrie);
		Arrays.stream(trieEntryList.getElementData()).
		parallel().
		forEach(te -> {
			String word = te.getTransformedValue().toLowerCase();
			char[] charArray = Arrays.copyOfRange(word.toCharArray(), 0, trieConfig.getMaximumWordLength());

//Is it worth it to use Streaming concept here?			
//			Stream<Character> cStream = IntStream.range(0, charArray.length).mapToObj(i -> charArray[i]);
//			final int teIndex = te.getIndex();
//			TrieNode[] newCurrent = new TrieNode[1];
//			newCurrent[0] = root;
//			cStream.forEach(ch -> {
//				if (trieConfig.getVocabulary().exists(ch))
//				{
//					if (ch == ' ')
//					{
//						newCurrent[0].markAsCompleteWord();			
//					}
//					newCurrent[0] = newCurrent[0].getOrElseCreate(ch);
//					newCurrent[0].addTrieEntryListIndex(teIndex);
//				}
//			});
//			newCurrent[0].markAsCompleteWord();

			TrieNode current = root;
			for (char ch : charArray)
			{
				if (trieConfig.getVocabulary().exists(ch))
				{
					if (ch == Vocabulary.TOKEN_DELIMITER)
					{
						current.markAsCompleteWord();			
					}
					current = current.getOrElseCreate(ch);
					current.addTrieEntryListIndex(te.getIndex());
				}
			}
			current.markAsCompleteWord();
		});
		trieStatistics.end(TrieMetrics.PopulateTrie);

		trieStatistics.start(TrieMetrics.IndexTrie);
		root.indexIt();
		trieStatistics.end(TrieMetrics.IndexTrie);
		trieStatistics.end(TrieMetrics.Readiness);
		indexed = true;
		trieStatistics.setDatasetSize(trieEntryList.size());
		traverse(WARM_UP);
		trieStatistics.clear();
		return indexed; 
	}

	public boolean isEmpty()
	{
		return root.isEmpty();
	}


	public TraverseResult traverse(String queryString)
	{
		trieStatistics.incrementCallCount();
		long startTimeInNanoSeconds = System.nanoTime();
		TraverseResult traverseResult = null;

		queryString = queryString.toLowerCase();

		char[] searchStringAsArray = queryString.toCharArray();

		if (trieConfig.useRecursiveTraverseAlgorithm())
		{
			TrieNode firstNode = null;
			
			if (trieConfig.getVocabulary().exists(searchStringAsArray[0]))
			{
				firstNode = root.get(searchStringAsArray[0]);
			}
			if (firstNode != null)
			{
				traverseResult = traverse(firstNode, searchStringAsArray, 0);
			}
		}
		else
		{
			traverseResult = traverseLoop(root, searchStringAsArray, 0);
		}
		
		long timeTakenInNanoSeconds = System.nanoTime() -  startTimeInNanoSeconds;
		if (traverseResult != null)
		{
			traverseResult.setSuggestions(suggestionEngine.suggest(traverseResult.getIndexesIntoTrieEntryList()));
		}
		else
		{
			/**
			 * There is nothing in our trieEntryList that
			 * starts with this characters
			 */
			traverseResult = new TraverseResult(name, queryString);
			trieStatistics.incrementEmptyResultCallCount();
		}
		traverseResult.setTimeTakenInNanoSeconds(timeTakenInNanoSeconds);
		trieStatistics.record(queryString, timeTakenInNanoSeconds);
		
		return traverseResult;
	}

	/**
	 * ---------------------------------Private Methods------------------------------------
	 */
	
	/**
	 * 
	 * @param currentNode
	 * @param word
	 * @param index
	 * @return
	 */
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
			result = new TraverseResult(name, new String(word), matchQuality, currentNode.getTotalIndexesCount(), currentNode.getIndexesIntoTrieEntryList(), currentNode.haveAnyChildren(), currentNode.isCompleteWord());
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
				/**
				 * The search word's next character is not present in out list.
				 * Ex: Search word is *cartz* and we have 
				 * Scenario 1 : cart 
				 * Scenario 2 : carton and cartoon. Post carT(currentNode) we do 
				 * not have any word starting with Z.
				 */
				result = new TraverseResult(name, new String(word), MatchQuality.None, currentNode.getTotalIndexesCount(), currentNode.getIndexesIntoTrieEntryList(), false, false);
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
				return new TraverseResult(name, new String(word), MatchQuality.None, currentNode.getTotalIndexesCount(), currentNode.getIndexesIntoTrieEntryList(), false, false);
			}
			currentNode = childNode;
		}
		MatchQuality matchQuality = currentNode.isCompleteWord()? MatchQuality.Exact : MatchQuality.Partial;
		return new TraverseResult(name, new String(word), matchQuality, currentNode.getTotalIndexesCount(), currentNode.getIndexesIntoTrieEntryList(), currentNode.haveAnyChildren(), currentNode.isCompleteWord());
	}
}
