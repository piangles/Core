package org.piangles.core.structures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.piangles.core.structures.impl2.Trie;
import org.piangles.core.structures.impl2.Vocabulary;

//https://github.com/eugenp/tutorials/blob/master/data-structures/src/test/java/com/baeldung/trie/TrieUnitTest.java
public class BitmapTrieTest2
{
	private static CharsetEncoder encoder =Charset.forName("US-ASCII").newEncoder();
	public static void main(String[] args) throws Exception
	{
		System.out.println(":" + Vocabulary.SPECIAL28 + ":" + Math.sqrt(Vocabulary.SPECIAL28));
		Trie trie = new Trie();
		File file = new File("C:\\Users\\sarad\\git\\Export\\10mwords.txt");

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
				//st = st.trim();
				//System.out.println("["+st+"]" + count);
				//System.out.println(count);

//				if (!st.matches("\\S+"))
//				{
//					continue;
////					String[] strs = st.split("\\S+");
////					//String[] strs = st.split(" ");
////					for (String str : strs)
////					{
////						trie.insert(str.trim());	
////					}
//				}
//				else
				{
					trie.insert(st);
				}
				//break;
			}
			else
			{
				skipCount++;
				System.out.println(st);
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
		trie.contains("3");
		trie.contains("vida");

		trie.contains("Programming");
		trie.contains("is");
		trie.contains("a");
		trie.contains("way");
		trie.contains("of");
		trie.contains("life");
		trie.contains("anal");
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
