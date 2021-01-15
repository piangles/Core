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

import java.io.File;

import org.piangles.core.util.LineProcessor;
import org.piangles.core.util.UTF8FileReader;;

public class BitmapTrieTest
{
	public static void main(String[] args) throws Exception
	{
		TrieConfig trieConfig = new TrieConfig();
		Trie trie = new Trie("Default", trieConfig);
		int searchNo = 1;
		File file = null;

		if (searchNo == 1)
		file = new File("./resources/1mwords.txt");
		else if (searchNo == 2)
		file = new File("./resources/6phrase.txt");
		else
		file = new File("C:\\Users\\sarad\\Downloads\\data.tsv");

		LineProcessor lp = (line, deaccentedLine, currentLineNo, percentageProcessed)->{
			if (searchNo == 3)
			{
				String[] values = line.split("\t");
				if (!values[3].equals("US"))
				{
					return;
				}
				else
				{
					line = values[2];
					deaccentedLine = values[2];
				}
			}
			trie.insert(new TrieEntry("" + currentLineNo, currentLineNo, line, deaccentedLine));

		};
		UTF8FileReader utf = new UTF8FileReader(file, true, lp);
		utf.processFile();

		trie.indexIt();
		long startTime = System.currentTimeMillis();
		long startTimeNano = System.nanoTime();

		if (searchNo == 1)
			search1MWords(trie);
		else if(searchNo == 2)
			search6PWords(trie);
		else
			searchMovies(trie);

		System.out.println("Look up Time Taken : " + (System.currentTimeMillis() - startTime) + " MiliSeconds.");
		System.out.println("Look up Time Taken : " + (System.nanoTime() - startTimeNano) + " NanoSeconds.\n");
		
		System.out.println(trie.getStatistics());
		memory();
	}

	private static void searchMovies(Trie trie) throws Exception
	{
		print(trie.traverse("the"));
		print(trie.traverse("the lord"));
		print(trie.traverse("the lord of the"));
		print(trie.traverse("of the"));
		print(trie.traverse("of the rings"));
		print(trie.traverse("rings"));
	}
	
	private static void search6PWords(Trie trie) throws Exception
	{
		// print(trieAngulator.search("ambi"));
		// print(trieAngulator.search("ambient"));
		print(trie.traverse("mu"));
		print(trie.traverse("music"));
		// print(trieAngulator.search("up"));
		print(trie.traverse("trunks"));
		print(trie.traverse("#"));
	}

	private static void search1MWords(Trie trie) throws Exception
	{
		print(trie.traverse("3"));
		print(trie.traverse("3r"));
		print(trie.traverse("vida"));

		print(trie.traverse("Programming"));
		print(trie.traverse("is"));
		print(trie.traverse("a"));
		print(trie.traverse("way"));
		print(trie.traverse("of"));
		print(trie.traverse("life"));
		print(trie.traverse("anal"));
		print(trie.traverse("anallise"));
		print(trie.traverse("{"));
	}

	private static void print(TraverseResult sr)
	{
		System.out.println("Search result for [" + sr.getQueryString() + "] : " + sr);
	}

	public static void memory()
	{
		int mb = 1024*1024;
		
		//Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();
		
		System.out.println("##### Heap utilization statistics [MB] #####");
		
		//Print used memory
		System.out.println("Used Memory:" 
			+ (runtime.totalMemory() - runtime.freeMemory()) / mb);

		//Print free memory
		System.out.println("Free Memory:" 
			+ runtime.freeMemory() / mb);
		
		//Print total available memory
		System.out.println("Total Memory:" + runtime.totalMemory() / mb);

		//Print Maximum available memory
		System.out.println("Max Memory:" + runtime.maxMemory() / mb);		
	}
}
