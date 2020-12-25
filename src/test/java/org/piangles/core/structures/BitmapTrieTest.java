package org.piangles.core.structures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import static org.piangles.core.structures.TrieConstants.PERF_MONITORING;;

public class BitmapTrieTest
{
	private static CharsetEncoder encoder =Charset.forName("US-ASCII").newEncoder();

	
	public static void main(String[] args) throws Exception
	{
		Trie trie = new Trie(1100000);
		File file = new File("C:\\Users\\sarad\\git\\Export\\1mwords.txt");

		long startTime = System.currentTimeMillis();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String st;
		long count = 0;
		int skipCount = 0;
		while ((st = br.readLine()) != null)
		{
			st = st.trim();
			if (isPureAscii(st) && st.length() > 0)
			{
				count++;
				trie.insert(st);
			}
			else
			{
				skipCount++;
				System.out.println(st);
			}
		}
		br.close();
		
		if (PERF_MONITORING) Thread.sleep(5000);
		System.out.println("Total number of words inscope: " + count + " Skipped : " + skipCount + " Time Taken : " + (System.currentTimeMillis() - startTime) + " MiliSeconds.");
		
		startTime = System.currentTimeMillis();
		trie.indexIt();
		System.out.println("Time Taken to Index : " + (System.currentTimeMillis() - startTime) + " MiliSeconds.");
		
		if (PERF_MONITORING) Thread.sleep(5000);

//		trie.insert("Programming");
//		trie.insert("Programmer");
//		trie.insert("is");
//		trie.insert("a");
//		trie.insert("way");
//		trie.insert("of");
//		trie.insert("life");

		startTime = System.currentTimeMillis();
		trie.search("3");
		trie.search("3r");
		trie.search("vida");

		trie.search("Programming");
		trie.search("is");
		trie.search("a");
		trie.search("way");
		trie.search("of");
		trie.search("life");
		trie.search("anal");
		trie.search("anallise");
		trie.search("{");
		System.out.println("Look up Time Taken : " + (System.currentTimeMillis() - startTime) + " MiliSeconds.");
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
