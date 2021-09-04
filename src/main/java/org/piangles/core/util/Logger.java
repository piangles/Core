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
 * 
 * Logger is used by Core classes and any can be used by any other class or service 
 * that is not having access to LoggingService during compile time. This way we can
 * avoid System.out.println in code. 
 * 
 * At runtime this class picks up environment variable logger.class to instantiate the 
 * actual Logger. If the environment variable is not defined. It will pick up 
 * 1. DefaultLogger : Which logs to Console.
 * 2. org.piangles.backbone.services.logging.LoggingServiceLogger : If configured will 
 * 		log to LoggingService.
 * 
 * The Accountant
 * Neurologist: Your son is different
 * Young Chris's Father: Sooner or later, different scares people.
 * 
 */
public abstract class Logger
{
	public static final String LOGGER_CLASS = "logger.class";
	public static final String INSTANCE = "instance";
	
	public static Logger logger = null;
	
	public abstract void debug(Object message);

	public abstract void debug(Object message, Throwable t);
	
	public abstract void info(Object message);

	public abstract void info(Object message, Throwable t);
	
	public abstract void warn(Object message);

	public abstract void warn(Object message, Throwable t);
	
	public abstract void error(Object message);

	public abstract void error(Object message, Throwable t);

	public abstract void fatal(Object message);

	public abstract void fatal(Object message, Throwable t);

	public static Logger getInstance()
	{
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
