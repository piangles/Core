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


package org.piangles.core.structures;

public final class TrieEntry
{
	private String id;
	private int index;
	private TrieEntry parent;
	private int rank;
	private String value;

	public TrieEntry(String value)
	{
		this(null, null, -1, value);
	}

	public TrieEntry(String id, int rank, String value)
	{
		this(null, null, rank, value);
	}

	public TrieEntry(String id, TrieEntry parent, int rank, String value)
	{
		this.id = id;
		this.parent = parent;
		this.rank = rank;
		this.value = value;
	}
	
	public String getId()
	{
		return id;
	}
	
	public int getRank()
	{
		return rank;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public boolean isDerived()
	{
		return parent != null;
	}
	
	TrieEntry getParent()
	{
		return parent;
	}
	
	void setIndex(int index)
	{
		this.index = index;
	}
	
	int getIndex()
	{
		return index;
	}
}
