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

public class DefaultLogger extends Logger
{
	public void debug(Object message)
	{
		record(message);
	}

	public void debug(Object message, Throwable t)
	{
		record(message, t);
	}

	public void info(Object message)
	{
		record(message);
	}

	public void info(Object message, Throwable t)
	{
		record(message, t);
	}
	
	public void warn(Object message)
	{
		record(message);
	}

	public void warn(Object message, Throwable t)
	{
		record(message, t);
	}
	
	public void error(Object message)
	{
		record(message);
	}

	public void error(Object message, Throwable t)
	{
		record(message, t);
	}

	public void fatal(Object message)
	{
		record(message);
	}

	public void fatal(Object message, Throwable t)
	{
		record(message, t);
	}

	private void record(Object message)
	{
		record(message, null);
	}
	
	private void record(Object message, Throwable t)
	{
		System.out.println(message);
		if (t != null)
		{
			t.printStackTrace(System.err);
		}
	}
}
