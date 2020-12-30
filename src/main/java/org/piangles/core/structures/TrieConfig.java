package org.piangles.core.structures;

public final class TrieConfig
{
	public static final int DEFAULT_INITIAL_SIZE = 1000000;
	public static final int DEFAULT_MAX_WORD_LENGTH = 20;
	public static final int DEFAULT_SUGGESTIONS_LIMIT = 10;
	public static final boolean DEFAULT_USE_RECURSIVE_ALGORITHM = true;
	public static final boolean DEFAULT_PERF_MONITORING = false;

	private int initialSize;
	private int maximumWordLength;
	private int suggestionsLimit;
	
	private boolean recursiveAlgorithm;
	private boolean performanceMonitoringEnabled;

	public TrieConfig()
	{
		this(DEFAULT_INITIAL_SIZE, DEFAULT_MAX_WORD_LENGTH, DEFAULT_SUGGESTIONS_LIMIT, DEFAULT_USE_RECURSIVE_ALGORITHM, DEFAULT_PERF_MONITORING);
	}
	
	public TrieConfig(int initialSize, int maximumWordLength, int suggestionsLimit, boolean recursiveAlgorithm, boolean performanceMonitoringEnabled)
	{
		this.initialSize = initialSize;
		this.maximumWordLength = maximumWordLength;
		this.suggestionsLimit = suggestionsLimit;
		this.recursiveAlgorithm = recursiveAlgorithm;
		this.performanceMonitoringEnabled = performanceMonitoringEnabled;
	}
	
	public int getInitialSize()
	{
		return initialSize;
	}

	public int getMaximumWordLength()
	{
		return maximumWordLength;
	}
	
	public int getSuggestionsLimit()
	{
		return suggestionsLimit;
	}
	
	public boolean useRecursiveAlgorithm()
	{
		return recursiveAlgorithm;
	}
	
	public boolean isPerformanceMonitoringEnabled()
	{
		return performanceMonitoringEnabled;
	}
}
