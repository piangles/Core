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
import java.util.Arrays;
import java.util.HashSet;

import org.piangles.core.util.LineProcessor;
import org.piangles.core.util.UTF8FileReader;;

public class BitmapTrieAngulationTest
{
	public static void main(String[] args) throws Exception
	{
		TrieConfig trieConfig = new TrieConfig();
		trieConfig.setIndexingTimeOutInSeconds(300);
		
		TrieAngulator trieAngulator = null;
		int searchNo = 1;
		File file = null;
		
		if (searchNo == 1)
		{
			trieAngulator = new TrieAngulator("1MM Words", new HashSet<String>(Arrays.asList("Attribute1", "Attribute2")), trieConfig);
			file = new File("./resources/1mwords.txt");
		}
		else if (searchNo == 2)
		{
			trieAngulator = new TrieAngulator("6 Phrase", trieConfig);
			file = new File("./resources/6phrase.txt");
		}
		else
		{
			trieAngulator = new TrieAngulator("Movies", trieConfig);
			file = new File("C:\\Users\\sarad\\Downloads\\data.tsv");
		}

		final TrieAngulator newTrieAngulator = trieAngulator; 
		LineProcessor lp = (actualLine, deaccentedLine, currentLineNo, percentageProcessed)->
		{
			if (searchNo == 3)
			{
				String[] actualValues = actualLine.split("\t");
				if (!actualValues[3].equals("US"))
				{
					return false;
				}
				else
				{
					String[] deaccentedValues = deaccentedLine.split("\t");

					actualLine = actualValues[2];
					deaccentedLine = deaccentedValues[2];
				}
			}

			//derivedWords = derivedWords + (int) st.chars().filter(c -> c == (int) ' ').count();
			if (searchNo == 1)
			{
				if (currentLineNo % 2 == 0)
				{
					newTrieAngulator.insert("Attribute1", new TrieEntry("" + currentLineNo, (int) currentLineNo, actualLine, deaccentedLine));	
				}
				else
				{
					newTrieAngulator.insert("Attribute2", new TrieEntry("" + currentLineNo, (int) currentLineNo, actualLine, deaccentedLine));
				}
			}
			else
			{
				newTrieAngulator.insert("Default", new TrieEntry("" + currentLineNo, (int) currentLineNo, actualLine, deaccentedLine));
			}
			
			return true;
		};
		UTF8FileReader utf = new UTF8FileReader(file, true, lp);
		utf.processFile();

		try
		{
			trieAngulator.start();
			long startTime = System.currentTimeMillis();
			long startTimeNano = System.nanoTime();

			if (searchNo == 1)
				search1MWords(trieAngulator);
			else if(searchNo == 2)
				search6PWords(trieAngulator);
			else
				searchMovies(trieAngulator);

			System.out.println("Look up Time Taken : " + (System.currentTimeMillis() - startTime) + " MiliSeconds.");
			System.out.println("Look up Time Taken : " + (System.nanoTime() - startTimeNano) + " NanoSeconds.\n");
		}
		finally
		{
			System.out.println(trieAngulator.getStatistics());
			memory();

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
		print(trieAngulator.trieangulate("Miami Expose"));
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
