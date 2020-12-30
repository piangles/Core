package org.piangles.core.structures;

/**
 * For Internal Use Only.
 * 
 * This class captures at Trie level performance metrics
 * 1. Universe/DataSet Size
 * 2. Memory Usage
 *
 */
final class TrieStatistics
{
	private int datasetSize;
	
	private long timeTakenToIndex;
	private long timeTakenToIndexNodes;
	private long timeTakenToSortDataset;
	
	private int maxMemoryInMB;
	private int totalMemoryInMB;
	private int freeMemoryInMB;
	private int usedMemoryInMB;
	
	public void memory()
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
