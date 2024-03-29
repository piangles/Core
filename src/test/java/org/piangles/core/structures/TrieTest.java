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
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.piangles.core.structures.hash.Trie;

//https://github.com/eugenp/tutorials/blob/master/data-structures/src/test/java/com/baeldung/trie/TrieUnitTest.java
public class TrieTest
{
	private static CharsetEncoder encoder =Charset.forName("US-ASCII").newEncoder(); 
	public static void main(String[] args) throws Exception
	{
		Trie trie = new Trie();

		File file = new File("./resources/1mwords.txt");

		long startTime = System.currentTimeMillis();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String st;
		long count = 0;
		int skipCount = 0;
		while ((st = br.readLine()) != null)
		{
			if (isPureAscii(st))
			{
				count++;
//				if (st.equalsIgnoreCase("Programming"))
//				{
//					System.out.println("HELLLO:" + st);
//				}
				trie.insert(st);
			}
			else
			{
				skipCount++;
			}
		}
		System.out.println("Total number of words inscope: " + count + " Skipped : " + skipCount + " Time Taken : " + (System.currentTimeMillis() - startTime));

//		trie.insert("Programming");
//		trie.insert("Programmer");
//		trie.insert("is");
//		trie.insert("a");
//		trie.insert("way");
//		trie.insert("of");
//		trie.insert("life");

		startTime = System.nanoTime();
		trie.containsNode("3");
		trie.containsNode("vida");

		trie.containsNode("Programming");
		trie.containsNode("is");
		trie.containsNode("a");
		trie.containsNode("way");
		trie.containsNode("of");
		trie.containsNode("life");
		trie.containsNode("anallise");
		System.out.println("Look up Time Taken : " + (System.nanoTime() - startTime));
		memory();
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

	private static void memory()
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
