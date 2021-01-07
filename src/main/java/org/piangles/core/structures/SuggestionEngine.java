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

final class SuggestionEngine
{
	private String context = null;
	private TrieEntryList trieEntryList = null;
	
	SuggestionEngine(String context, TrieEntryList trieEntryList)
	{
		this.context = context;
		this.trieEntryList = trieEntryList;
	}
	
	public Suggestion[] suggestTopTen()
	{
		return null;
	}
	
	public Suggestion[] suggest(int[] indexesIntoTrieEntryList)
	{
		TrieEntry te = null;
		Suggestion[] suggestions = new Suggestion[indexesIntoTrieEntryList.length];
		for (int i=0; i < indexesIntoTrieEntryList.length; ++i)
		{
			te = trieEntryList.get(indexesIntoTrieEntryList[i]);
			if (te.isDerived())
			{
				te = trieEntryList.get(te.getParent().getIndex());
			}
			suggestions[i] = new Suggestion(context, te.getId(), te.getValue());
		}
		return suggestions;
	}
}
