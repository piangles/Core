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
 
 
package org.piangles.core.util.instrument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InstrumentationConductor
{
	public static final int WAIT_TIME_BETWEEN_QUERY = 60 * 1000;
	
	private static InstrumentationConductor self = null;

	private String name = null;
	private Map<String, Instrumentator> instrumentatorMap = null;
	private Map<String, List<InstrumentationCallback>> callbacksMap = null;
	private boolean defaultCallbackEnabled = true;
	private boolean stop = false;
	
	private InstrumentationConductor(String name)
	{
		this.name = name;
		this.instrumentatorMap = new HashMap<>();
		this.callbacksMap = new HashMap<>();
	}
	
	public synchronized static void createInstance(String name)
	{
		if (self == null)
		{
			self = new InstrumentationConductor(name);
			
			self.registerInstrumentator(new SystemInstrumentator(name));
			self.registerInstrumentator(new PerformanceInstrumentator(name));
			self.registerInstrumentator(new MemoryInstrumentator(name));
		}
	}
	
	public static InstrumentationConductor getInstance()
	{
		return self;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void start()
	{
		new InstrumentationThread(name).start();
	}
	
	public void registerInstrumentator(Instrumentator instrumentator)
	{
		instrumentatorMap.put(instrumentator.getName(), instrumentator);
	}
	
	public Instrumentator getInstrumentator(String instrumentatorName)
	{
		return instrumentatorMap.get(instrumentatorName);
	}
	
	public void disableDefaultCallback()
	{
		defaultCallbackEnabled = false;
	}
	
	public synchronized void addCallback(String measuresName, InstrumentationCallback ic)
	{
		List<InstrumentationCallback> callbackList = callbacksMap.get(measuresName);
		if (callbackList == null)
		{
			callbackList = new ArrayList<>();
			callbacksMap.put(measuresName, callbackList);
		}
		callbackList.add(ic);
	}
	
	synchronized void notifyCallbacks(Measures measures)
	{
		if (defaultCallbackEnabled)
		{
			//Default Callback will just print on screen
			System.out.println(measures + "\n");
		}
		
		List<InstrumentationCallback> callbackList = callbacksMap.get(measures.getName());
		if (callbackList != null)
		{
			for (InstrumentationCallback ic : callbackList)
			{
				ic.onInstrumentation(measures);
			}
		}
	}

	public void stop()
	{
		stop = true;
	}

	class InstrumentationThread extends Thread
	{
		public InstrumentationThread(String name)
		{
			super(name);
		}
		
		@Override
		public void run()
		{
			List<Measures> measuresList = new ArrayList<>();
			while (!stop)
			{
				measuresList.clear();
				
				//Measure
				for (String measureName : instrumentatorMap.keySet())
				{
					try
					{
						Measures measures = instrumentatorMap.get(measureName).doInstrumentation();
						measures.markRecordedTimestamp();
						measuresList.add(measures);
					}
					catch(Throwable t)
					{
						System.err.println("InstrumentationThread: Exception while instrumenting : " + t.getMessage());
						t.printStackTrace(System.err);
					}
				}

				//Notify Callbacks
				for (Measures measures : measuresList)
				{
					try
					{
						notifyCallbacks(measures);
					}
					catch(Throwable t)
					{
						System.err.println("InstrumentationThread: Exception while calling back : " + t.getMessage());
						t.printStackTrace(System.err);
					}
				}
				
				try
				{
					Thread.sleep(WAIT_TIME_BETWEEN_QUERY);
				}
				catch (InterruptedException e)
				{
					System.err.println("InstrumentationThread: Exception while sleeping : " + e.getMessage());
					e.printStackTrace(System.err);
				}
			}
		}
	}
}
