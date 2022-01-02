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

final class DefaultSuggestionEngine implements SuggestionEngine
{
	private String attribute = null;
	private TrieEntryList trieEntryList = null;
	
	public void init(String attribute, TrieEntryList trieEntryList)
	{
		this.attribute = attribute;
		this.trieEntryList = trieEntryList;
	}
	
	@Override
	public Suggestion[] suggest(int[] indexesIntoTrieEntryList)
	{
		Suggestion[] suggestions = new Suggestion[indexesIntoTrieEntryList.length];

		TrieEntry trieEntry = null;
		for (int i=0; i < indexesIntoTrieEntryList.length; ++i)
		{
			trieEntry = trieEntryList.get(indexesIntoTrieEntryList[i]);
			if (trieEntry.isDerived())
			{
				trieEntry = trieEntryList.get(trieEntry.getParent().getIndex());
			}
			suggestions[i] = new Suggestion(trieEntry.getId(), attribute, trieEntry.getActualValue());
		}
		
		return suggestions;
	}
}
