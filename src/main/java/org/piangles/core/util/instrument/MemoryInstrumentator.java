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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public final class MemoryInstrumentator  extends AbstractInstrumentator
{
	private static final String NAME = "MemoryDetails";
	private static final int MEGA_BYTE = 1024 * 1024;

	private MemoryMXBean memBean = null;
	private MemoryUsage heap = null;
	private MemoryUsage nonHeap = null;
	private Runtime runtime = null;

	private Measures memoryDetails = null;
	
	public MemoryInstrumentator(String serviceName)
	{
		super(NAME);
		
		memBean = ManagementFactory.getMemoryMXBean() ;

	    heap = memBean.getHeapMemoryUsage();
	    nonHeap = memBean.getNonHeapMemoryUsage();
	    runtime = Runtime.getRuntime();
	    
	    memoryDetails = new Measures(NAME, serviceName); 
	}

	@Override
	public Measures doInstrumentation()
	{
		memoryDetails.clear(); 
		
	    // heap
		memoryDetails.addMeasure("HeapInit", heap.getInit());
		memoryDetails.addMeasure("HeapCommitted", heap.getCommitted());
	    
		memoryDetails.addMeasure("HeapMax", heap.getMax());
		memoryDetails.addMeasure("HeapUsed", heap.getUsed());
	    
	    // non-heap
		memoryDetails.addMeasure("NonHeapInit", nonHeap.getInit());
		memoryDetails.addMeasure("NonHeapMax", nonHeap.getMax());
		memoryDetails.addMeasure("NonHeapUsed", nonHeap.getUsed());
		memoryDetails.addMeasure("NonHeapCommitted", nonHeap.getCommitted());		

		// Getting the runtime reference from system

		// Print used memory
		memoryDetails.addMeasure("Used Memory", (runtime.totalMemory() - runtime.freeMemory()) / MEGA_BYTE);

		// Print free memory
		memoryDetails.addMeasure("Free Memory", (runtime.freeMemory() / MEGA_BYTE));

		// Print total available memory
		memoryDetails.addMeasure("Total Memory", (runtime.totalMemory() / MEGA_BYTE));

		// Print Maximum available memory
		memoryDetails.addMeasure("Max Memory", (runtime.maxMemory() / MEGA_BYTE));

		return memoryDetails;
	}
}
