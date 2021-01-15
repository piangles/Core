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

import java.io.Serializable;

public final class TrieEntry implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String id;
	private int index;
	private TrieEntry parent;
	private int rank;
	private String value;
	private String actualValue;

	public TrieEntry(String value, String actualValue)
	{
		this(null, null, -1, value, actualValue);
	}

	public TrieEntry(String id, int rank, String value, String actualValue)
	{
		this(null, null, rank, value, actualValue);
	}

	public TrieEntry(String id, TrieEntry parent, int rank, String value, String actualValue)
	{
		this.id = id;
		this.parent = parent;
		this.rank = rank;
		this.value = value;
		this.actualValue = actualValue;
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

	public String getActualValue()
	{
		return actualValue;
	}

	public boolean isDerived()
	{
		return parent != null;
	}
	
    /**
     * @return  a negative integer, zero, or a positive integer as the
     *          specified String is greater than, equal to, or less
     *          than this String, ignoring case considerations.
     */
    public int compare(TrieEntry other)
    {
    	int compareResult = 0;
    	if (!this.isDerived() && !other.isDerived())
    	{
    		/**
    		 * Use Rank first to compare and in absence of Rank 
    		 * use lexical sorting.
    		 */
    		if (this.rank != -1 && other.rank != -1)
    		{
    			compareResult = (this.rank == other.rank)? 0 : (this.rank > other.rank)  ? 1 : -1;
    		}
    		else
    		{
        		compareResult = this.value.compareToIgnoreCase(other.value); 
    		}
    	}
    	else if (this.isDerived() && other.isDerived())
    	{
    		compareResult = this.parent.compare(other.parent); 
    	}
    	else if (this.isDerived())
    	{
    		compareResult = 1;
    	}
    	else if (other.isDerived())
    	{
    		compareResult = -1;
    	}
        return compareResult;
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
	
	@Override
	public String toString()
	{
		return value;
	}
}
