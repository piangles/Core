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

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class Measures implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String serviceName;
	private boolean oneTimeMeasure;
	private Map<String, Object> measureMap;
	private Date recordedTimestamp = null;
	
	public Measures(String name, String serviceName, boolean oneTimeMeasure)
	{
		this.name = name;
		this.serviceName = serviceName;
		this.oneTimeMeasure = oneTimeMeasure;
		this.measureMap  = new LinkedHashMap<>(); 
	}
	
	public String getName()
	{
		return name;
	}

	public String getServiceName()
	{
		return serviceName;
	}

	public boolean isOneTimeMeasure()
	{
		return oneTimeMeasure;
	}
	
	public void clear()
	{
		measureMap.clear();
	}
	
	void markRecordedTimestamp()
	{
		recordedTimestamp = new Date();
	}
	
	public void addMeasure(String key, Object value)
	{
		measureMap.put(key, value);
	}
	
	public Map<String, Object> getMap()
	{
		return measureMap;
	}
	
	public Set<String> getKeys()
	{
		return measureMap.keySet();
	}
	
	public Object getValue(String key)
	{
		return measureMap.get(key);
	}
	
	public Date getRecordedTimestamp()
	{
		return recordedTimestamp;
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Service Name: ").append(serviceName).append("\n");
		sb.append("Measures Name: ").append(name).append("\n");
		sb.append("Recorded Timestamp: ").append(recordedTimestamp).append("\n");
		sb.append("Values: ").append("\n");
		for (String key : getKeys())
		{
			sb.append(key).append(": ").append(getValue(key)).append("\n");	
		}
		return sb.toString();
	}
}
