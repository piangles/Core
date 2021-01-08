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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;;

public class BitmapTrieTest
{
	private static CharsetEncoder encoder = Charset.forName("US-ASCII").newEncoder();

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

		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
		BufferedReader br = new BufferedReader(isr);

		long startTime = System.currentTimeMillis();
		// BufferedReader br = new BufferedReader(new FileReader(file));
		String st;
		long count = 0;
		int skipCount = 0;
		while ((st = br.readLine()) != null)
		{
			st = st.trim();
		
			if (searchNo == 3)
			{
				String[] values = st.split("\t");
				if (!values[3].equals("US")) continue; else st = values[2];
			}

			//derivedWords = derivedWords + (int) st.chars().filter(c -> c == (int) ' ').count();
			if (isPureAscii(st) && st.length() > 0)
			{
				count++;
				trie.insert(new TrieEntry("" + count, (int) count, st));
				// trieAngulator.insert(new TrieEntry(st));
				// trieAngulator.insert(st);
			}
			else
			{
				skipCount++;
				// System.out.println(st);
			}
		}
		br.close();
		System.out.println(
				"Total number of lines inscope: " + count + " Skipped : " + skipCount + " Time Taken : " + (System.currentTimeMillis() - startTime) + " MiliSeconds.");

		trie.indexIt();
		startTime = System.currentTimeMillis();
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

	public static boolean isAlpha(String name)
	{
		char[] chars = name.toCharArray();

		for (char c : chars)
		{
			if (!Character.isLetter(c))
			{
				return false;
			}
		}

		return true;
	}

	public static boolean isPureAscii(String v)
	{
		return encoder.canEncode(v);
		// or "ISO-8859-1" for ISO Latin 1
		// or StandardCharsets.US_ASCII with JDK1.7+
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
