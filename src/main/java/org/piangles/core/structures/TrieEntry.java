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

/**
 * Purpose of the fields in this class
 * 
 * id				:	Id associated with this record, the application can actually look up or details.
 * rank				: 	Rank is assigned during creation and remains immutable. It is upto the
 * 						application will determine the logic of deciding the Rank.
 * actualValue		:	The value that will be returned as suggestion. 
 * transformedValue	:	The value that will be used to breakdown and create the Trie. This could
 * 						be the same as the actualValue or deaccentedValue.
 * 
 * The below two are set by Trie while indexing.
 * 
 * index			:	
 * parent			:
 */
public final class TrieEntry implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String id;
	private int rank;
	private String actualValue;
	private String transformedValue;

	private int index;
	private TrieEntry parent;

	public TrieEntry(String actualValue, String transformedValue)
	{
		this(null, null, -1, actualValue, transformedValue);
	}

	public TrieEntry(String id, int rank, String actualValue, String transformedValue)
	{
		this(id, null, rank, actualValue, transformedValue);
	}

	/**
	 * This constructor should remain in Default Package visibility.
	 */
	TrieEntry(String id, TrieEntry parent, int rank, String actualValue, String transformedValue)
	{
		this.id = id;
		this.parent = parent;
		this.rank = rank;
		this.actualValue = actualValue;
		this.transformedValue = transformedValue;
	}
	
    /**
     * @param other The one that this TriEntry is being compared to.
     * 
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
        		compareResult = this.transformedValue.compareToIgnoreCase(other.transformedValue); 
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
	

	public String getId()
	{
		return id;
	}
	
	public int getRank()
	{
		return rank;
	}
	
	public String getActualValue()
	{
		return actualValue;
	}

	public String getTransformedValue()
	{
		return transformedValue;
	}

	public boolean isDerived()
	{
		return parent != null;
	}

	@Override
	public String toString()
	{
		return actualValue;
	}

	/**
	 * The following methods are only visible at package level as they are
	 * used by Trie and InMemoryTrieEntryList. 
	 * 
	 */
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
