package com.TBD.core.util;

/**
 * Java 8.0
 * Index 0 = java.lang.Thread.getStackTrace(Thread.java:1552)
 * Index 1 = com.TBD.backbone.util.ClassHelper.init(ClassHelper.java:46)
 * Index 2 = com.TBD.backbone.util.ClassHelper.<init>(ClassHelper.java: 20 or 25) depends on constructor
 * We can skip these 3.
 * Anything more we need to just add
 */
public class ClassHelper
{
	private static final int DEFAULT_SKIP = 3;
	private StringBuffer completeStackTrace = null;
	private String className = null;
	private String lineNumber = null;
	
	public ClassHelper()
	{
		init(0);
	}
	
	public ClassHelper(int indexToSkip)
	{
		init(indexToSkip);
	}
	
	public String getClassName()
	{
		return className;
	}
	
	public String getLineNumber()
	{
		return lineNumber;
	}
	
	public String getCompleteStackTrace()
	{
		return completeStackTrace.toString();
	}
	
	private void init(int indexToSkip)
	{
		completeStackTrace = new StringBuffer();
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement stackTrace = stackTraceElements[DEFAULT_SKIP+indexToSkip];
		className =  stackTrace.getClassName();
		lineNumber = "" + stackTrace.getLineNumber();

		for (int i=DEFAULT_SKIP + indexToSkip; i < stackTraceElements.length; ++i)
		{
			completeStackTrace.append(stackTraceElements[i]).append("\n");
		}
	}
}
