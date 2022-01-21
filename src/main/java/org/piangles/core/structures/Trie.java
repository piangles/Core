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

import org.apache.commons.lang3.StringUtils;
import org.piangles.core.util.Logger;

/**
 * This is the mother of all TrieNodes, one starts with creation of Trie.
 * Creation of Trie is dirven by 2 parameters.
 * 1. Name of the Trie : A name that describes the Dataset.
 * 2. TrieConfig : That governs the various parameters for Trie to organize it's structure.
 * 
 * This is an highly performant search algorithm that is Trie based and leverages Bitmap Based Trie 
 * Once a Trie is indexed it will remain immutable through it's existence. 
 */
public final class Trie implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static final String PIANGLES_ENV_STRUCTURES_LOG = "piangles.env.structures.log";
	
	private static final String WARM_UP = "az";
	
	private String name;
	private TrieConfig trieConfig = null;
	private TrieStatistics trieStatistics = null;
	
	private TrieNode root = null;
	private boolean indexed = false;
	
	private Map<String, String> stopWordsMap = null; 
	private TrieEntryList trieEntryList = null;
	
	private SuggestionEngine suggestionEngine = null;
	
	private boolean piAnglesLogEnabled = false;
	
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
		
		suggestionEngine = trieConfig.getSuggestionEngine();
		suggestionEngine.init(name, trieEntryList);
		
		String piAnglesLogEnabledStr = System.getenv(PIANGLES_ENV_STRUCTURES_LOG);
		if (StringUtils.isNotBlank(piAnglesLogEnabledStr))
		{
			piAnglesLogEnabled = Boolean.parseBoolean(piAnglesLogEnabledStr);
		}
	}
	
	/**
	 * Returns the name of the DataSet that it holds.
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * TODO
	 * @return
	 */
	public boolean isEmpty()
	{
		return root.isEmpty();
	}

	/**
	 * Each Trie records statistics through the course of it's existence
	 * Details of various measures are documented in TrieStatistics. 
	 * @return
	 */
	public TrieStatistics getStatistics()
	{
		return trieStatistics; 
	}
	
	/**
	 * This is one and only method that is provided to add TrieEntries into the
	 * Trie before it is indexed. Once indexed, the Trie is immutable and will
	 * throw  IllegalStateException if called after indexing.
	 * @param te
	 */
	public void insert(TrieEntry te)
	{
		if (indexed)
		{
			throw new IllegalStateException("Trie is immutable once it has been indexed.");
		}
		
		
		/**
		 * First add the TrieEntry containing the whole string into the list.
		 */
		trieEntryList.add(te);
		
		if (te.getTransformedValue().indexOf(Vocabulary.TOKEN_DELIMITER) != -1)
		{
			/**
			 * The String is not a word but a phrase since it contains ' '.
			 * Split up the string into parts containing individual words. 
			 */
			String[] splits = te.getTransformedValue().split("\\s+");
			for (int i=1; i < splits.length; ++i)
			{
				/**
				 * Skip pithy words such as [and, it, of...]
				 * these words are of no use for searching and take up memory.
				 * 
				 * Create a new Entry for each of the word with the same Id and 
				 * Rank. These are not transformed again as we already are using
				 * transformed value.
				 * 
				 * Update stats accordingly.
				 */
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

	/**
	 * Before a Trie is ready for traversing, it needs to organize and index all
	 * this TrieEntries. Once indexed, the Trie is immutable and is ready for
	 * traversal.
	 * 
	 * @return
	 */
	public synchronized boolean indexIt()
	{
		if (indexed)
		{
			throw new IllegalStateException("Trie has already been indexed.");
		}
		
		trieStatistics.start(TrieMetrics.Readiness);
		trieStatistics.start(TrieMetrics.SortDataset);

		/**
		 * To begin with trim the size of entryList to the actual size of the list.
		 * This ensures execess memory allocated is released back to the system.
		 */
		trieEntryList.trimToSize();
		
		/**
		 * Before we Index, sorting is required this way all the various trie entries
		 * are organized in a manner that makes indexing fast.
		 * Ex:
		 * Unsorted List
		 * 1. Car
		 * 2. Monday
		 * 3. Cart
		 * 4. Monkey
		 * 5. Carton
		 * 6. Money
		 * 
		 * For the above list, the indexing becomes very slow with each word needing to be
		 * traversed back to root and starting from the top. When we sort.
		 * Ex:
		 * Sorted List
		 * 1. Car
		 * 2. Cart
		 * 3. Carton
		 * 4. Monday
		 * 5. Money
		 * 6. Monkey
		 * So in terms of efficiency, the indexing does not have to go
		 * back to root until the 4th word.
		 */
		trieEntryList.sort();
		
		trieStatistics.end(TrieMetrics.SortDataset);
		
		trieStatistics.start(TrieMetrics.PopulateTrie);
		Arrays.stream(trieEntryList.getElementData()).
		parallel().
		forEach(te -> {
			/**
			 * The value can be phrase or a word, if the original value was:
			 * Hello World, it gets split prior to indexing as
			 * 1. Hello World
			 * 2. Hello
			 * 3. World
			 */
			String wordOrPhrase = te.getTransformedValue().toLowerCase();
			char[] charArray = Arrays.copyOfRange(wordOrPhrase.toCharArray(), 0, trieConfig.getMaximumWordLength());

			/**
			 * Is it worth it to use Streaming concept here?
			 * 
			 * Attempts have not shown significant benefits.
			 * 
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
			 *  
			 */
			

			TrieNode current = root;
			/**
			 * For each character in the Value, we determine if it is part of our
			 * Vocabulary. There is no way of indexing it if it not part of our
			 * Vocabulary.
			 */
			for (char ch : charArray)
			{
				if (trieConfig.getVocabulary().exists(ch))
				{
					/**
					 * If the character array is coming from a Phrase, we will 
					 * find the delimiter ' ' (space) and we need to mark as complete.
					 */
					if (ch == Vocabulary.TOKEN_DELIMITER)
					{
						current.markAsCompleteWord();			
					}
					/**
					 * CREATION
					 * --------
					 * This where we create the Nodes for each character of the word.
					 */
					current = current.getOrElseCreate(ch);
					current.addTrieEntryListIndex(te.getIndex());
				}
				else
				{
					/**
					 * {"log":"For TrieEntry with Id: e37a753 TransformedValue: A Skipping Character: \u0000 do not exist in Vocabulary.\n","stream":"stdout","time":"2022-01-14T13:33:36.29294099Z"}
					 * {"log":"For TrieEntry with Id: 56a1790 TransformedValue: TestingNine Skipping Character: \u0000 do not exist in Vocabulary.\n","stream":"stdout","time":"2022-01-14T13:33:36.292944712Z"}
					 * 
					 * https://stackoverflow.com/questions/12195628/understanding-the-difference-between-null-and-u000-in-java
					 * 
					 * If Null is being passed as part of the charArray then it is printing and taking up too much log space. 
					 * 
					 * https://stackoverflow.com/questions/14844793/how-do-i-compare-a-character-to-check-if-it-is-null
					 */
					if ((int)ch != 0)
					{
						if (piAnglesLogEnabled)
						{
							Logger.getInstance().warn("For TrieEntry with Id: " + te.getId() + " TransformedValue: " + te.getTransformedValue() + " Skipping Character: " + ch + " do not exist in Vocabulary.");
						}
					}
				}
			}
			//End of character array mark as complete word. 
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
		
		/**
		 * FINALLY
		 * SetSuggestions by look up indexes from the traversal.
		 */
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
			result = new TraverseResult(name, new String(word), matchQuality, currentNode.getTotalIndexesCount(), 
										currentNode.getIndexesIntoTrieEntryList(), currentNode.haveAnyChildren(), currentNode.isCompleteWord());
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
				result = new TraverseResult(name, new String(word), MatchQuality.None, 
											currentNode.getTotalIndexesCount(), currentNode.getIndexesIntoTrieEntryList(), false, false);
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
