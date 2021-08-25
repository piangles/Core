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
 
 
 
package org.piangles.core.util;

/**
 * Java 8.0
 * Index 0 = java.lang.Thread.getStackTrace(Thread.java:1552)
 * Index 1 = org.piangles.backbone.util.ClassHelper.init(ClassHelper.java:46)
 * Index 2 = org.piangles.backbone.util.ClassHelper.Constructor(ClassHelper.java: 20 or 25) depends on constructor
 * We can skip these 3.
 * Anything more we need to just add
 */
public class ClassHelper
{
	private static final int DEFAULT_SKIP = 2;
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
