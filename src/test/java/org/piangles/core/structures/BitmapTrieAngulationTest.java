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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;;

public class BitmapTrieAngulationTest
{
	private static CharsetEncoder encoder = Charset.forName("US-ASCII").newEncoder();

	public static void main(String[] args) throws Exception
	{
		TrieConfig trieConfig = new TrieConfig();
		TrieAngulator trieAngulator = null;
		int searchNo = 2;
		File file = null;
		
		if (searchNo == 1)
		{
			trieAngulator = new TrieAngulator("TestDataset", new HashSet<String>(Arrays.asList("Attribute1", "Attribute2")), trieConfig);
			file = new File("./resources/1mwords.txt");
		}
		else if (searchNo == 2)
		{
			trieAngulator = new TrieAngulator("TestDataset", trieConfig);
			file = new File("./resources/6phrase.txt");
		}
		else
		{
			trieAngulator = new TrieAngulator("TestDataset", trieConfig);
			file = new File("C:\\Users\\sarad\\Downloads\\data.tsv");
		}

		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
		BufferedReader br = new BufferedReader(isr);

		long startTime = System.currentTimeMillis();
		// BufferedReader br = new BufferedReader(new FileReader(file));
		String st;
		long count = 0;
		int skipCount = 0;
		int derivedWords = 0;
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
				if (searchNo == 1)
				{
					if (count % 2 == 0)
					{
						trieAngulator.insert("Attribute1", new TrieEntry("" + count, (int) count, st));	
					}
					else
					{
						trieAngulator.insert("Attribute2", new TrieEntry("" + count, (int) count, st));
					}
				}
				else
				{
					trieAngulator.insert("Default", new TrieEntry("" + count, (int) count, st));
				}
			}
			else
			{
				skipCount++;
				// System.out.println(st);
			}
		}
		br.close();
		System.out.println(
				"Total number of lines inscope: " + count + " Derived : " + derivedWords + " Skipped : " + skipCount + " Time Taken : " + (System.currentTimeMillis() - startTime) + " MiliSeconds.");

		try
		{
			trieAngulator.start();
			if (searchNo == 1)
			System.out.println(trieAngulator.getStatistics("Attribute1"));
			else
				System.out.println(trieAngulator.getStatistics());	
			startTime = System.currentTimeMillis();
			long startTimeNano = System.nanoTime();

			if (searchNo == 1)
				search1MWords(trieAngulator);
			else if(searchNo == 2)
				search6PWords(trieAngulator);
			else
				searchMovies(trieAngulator);

			System.out.println("Look up Time Taken : " + (System.currentTimeMillis() - startTime) + " MiliSeconds.");
			System.out.println("Look up Time Taken : " + (System.nanoTime() - startTimeNano) + " NanoSeconds.");
			
			if (searchNo == 1)
			{
				trieAngulator.getStatistics("Attribute1").memory();
			}
			else
			{
				trieAngulator.getStatistics().memory();
			}
		}
		finally
		{
			trieAngulator.stop();
		}
	}

	private static void searchMovies(TrieAngulator trieAngulator) throws Exception
	{
		print(trieAngulator.trieangulate("the"));
		print(trieAngulator.trieangulate("the lord"));
		print(trieAngulator.trieangulate("the lord of the"));
		print(trieAngulator.trieangulate("of the"));
		print(trieAngulator.trieangulate("of the rings"));
		print(trieAngulator.trieangulate("rings"));
	}
	
	private static void search6PWords(TrieAngulator trieAngulator) throws Exception
	{
		// print(trieAngulator.search("ambi"));
		// print(trieAngulator.search("ambient"));
		print(trieAngulator.trieangulate("mu"));
		print(trieAngulator.trieangulate("music"));
		// print(trieAngulator.search("up"));
		print(trieAngulator.trieangulate("trunks"));
		print(trieAngulator.trieangulate("#"));
	}

	private static void search1MWords(TrieAngulator trieAngulator) throws Exception
	{
		print(trieAngulator.trieangulate("3"));
		print(trieAngulator.trieangulate("3r"));
		print(trieAngulator.trieangulate("vida"));

		print(trieAngulator.trieangulate("Programming"));
		print(trieAngulator.trieangulate("is"));
		print(trieAngulator.trieangulate("a"));
		print(trieAngulator.trieangulate("way"));
		print(trieAngulator.trieangulate("of"));
		print(trieAngulator.trieangulate("life"));
		print(trieAngulator.trieangulate("anal"));
		print(trieAngulator.trieangulate("anallise"));
		print(trieAngulator.trieangulate("{"));
	}

	private static void print(TrieAngulationResult sr)
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
}
