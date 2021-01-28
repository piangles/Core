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

import java.util.HashMap;
import java.util.Map;

public interface Logger
{
	public static final String LOGGER_CLASS = "logger.class";
	public static final String INSTANCE = "instance";
	
	public static final Map<String, Logger> loggerMap = new HashMap<>();
	
	public void debug(Object message);

	public void debug(Object message, Throwable t);
	
	public void info(Object message);

	public void info(Object message, Throwable t);
	
	public void warn(Object message);

	public void warn(Object message, Throwable t);
	
	public void error(Object message);

	public void error(Object message, Throwable t);

	public void fatal(Object message);

	public void fatal(Object message, Throwable t);

	public static Logger getInstance()
	{
		Logger logger = loggerMap.get(INSTANCE);
		if (logger == null)
		{
			String loggerClassName = System.getenv(LOGGER_CLASS);
			if (loggerClassName == null)
			{
				loggerClassName = DefaultLogger.class.getCanonicalName();
				System.err.println(LOGGER_CLASS + " property is NOT set, defaulting to : " + loggerClassName);
			}
			else
			{
				System.out.println(LOGGER_CLASS + " property is set, trying to create : " + loggerClassName);
			}
			try
			{
				logger = (Logger)Class.forName(loggerClassName).newInstance();
				loggerMap.put(INSTANCE, logger);
			}
			catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
			{
				System.err.println("Unable to create : " + loggerClassName + " because: " + e.getMessage());
				e.printStackTrace(System.err);
				throw new RuntimeException(e);
			}
		}
		
		return logger;
	}
}
