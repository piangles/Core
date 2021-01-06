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

import java.util.Arrays;
import java.util.Collection;

final class SimpleArray
{
	/**
	 * Default initial capacity.
	 */
	private static final int DEFAULT_CAPACITY = 100000;

	/**
	 * The maximum size of array to allocate. Some VMs reserve some header words
	 * in an array. Attempts to allocate larger arrays may result in
	 * OutOfMemoryError: Requested array size exceeds VM limit
	 */
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	/**
	 * Shared empty array instance used for empty instances.
	 */
	private static final TrieEntry[] EMPTY_ELEMENTDATA = {};

	/**
	 * Shared empty array instance used for default sized empty instances. We
	 * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
	 * first element is added.
	 */
	private static final TrieEntry[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

	/**
	 * The array buffer into which the elements of the ArrayList are stored. The
	 * capacity of the ArrayList is the length of this array buffer. Any empty
	 * ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA will be
	 * expanded to DEFAULT_CAPACITY when the first element is added.
	 */
	private transient TrieEntry[] elementData; 

	/**
	 * The size of the ArrayList (the number of elements it contains).
	 *
	 * @serial
	 */
	private int size;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 *
	 * @param initialCapacity
	 *            the initial capacity of the list
	 * @throws IllegalArgumentException
	 *             if the specified initial capacity is negative
	 */
	public SimpleArray(int initialCapacity)
	{
		if (initialCapacity > 0)
		{
			this.elementData = new TrieEntry[initialCapacity];
		}
		else
		{
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		}
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * @param e
	 *            element to be appended to this list
	 * @return <tt>true</tt> (as specified by {@link Collection#add})
	 */
	public int add(TrieEntry e)
	{
		ensureCapacityInternal(size + 1); // Increments modCount!!
		elementData[size++] = e;
		return size - 1;
	}

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param index
	 *            index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public TrieEntry get(int index)
	{
		return elementData[index];
	}

	public TrieEntry[] subsetIfPossible(int index, int elements)
	{
		return Arrays.copyOfRange(elementData, 1, elementData.length);
	}

	/**
	 * Trims the capacity of this <tt>ArrayList</tt> instance to be the list's
	 * current size. An application can use this operation to minimize the
	 * storage of an <tt>ArrayList</tt> instance.
	 */
	public void trimToSize()
	{
		if (size < elementData.length)
		{
			elementData = (size == 0) ? EMPTY_ELEMENTDATA : Arrays.copyOf(elementData, size);
		}
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return the number of elements in this list
	 */
	public int size()
	{
		return size;
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 *
	 * @return <tt>true</tt> if this list contains no elements
	 */
	public boolean isEmpty()
	{
		return size == 0;
	}

	/**
	 * Returns an array containing all of the elements in this list in proper
	 * sequence (from first to last element).
	 *
	 * <p>
	 * The returned array will be "safe" in that no references to it are
	 * maintained by this list. (In other words, this method must allocate a new
	 * array). The caller is thus free to modify the returned array.
	 *
	 * <p>
	 * This method acts as bridge between array-based and collection-based APIs.
	 *
	 * @return an array containing all of the elements in this list in proper
	 *         sequence
	 */
	public Object[] toArray()
	{
		return Arrays.copyOf(elementData, size);
	}

	/**
	 * Returns a shallow copy of this <tt>ArrayList</tt> instance. (The elements
	 * themselves are not copied.)
	 *
	 * @return a clone of this <tt>ArrayList</tt> instance
	 */
	public Object clone()
	{
		try
		{
			SimpleArray v = (SimpleArray) super.clone();
			v.elementData = Arrays.copyOf(elementData, size);
			return v;
		}
		catch (CloneNotSupportedException e)
		{
			// this shouldn't happen, since we are Cloneable
			throw new InternalError(e);
		}
	}

	/**
	 * Removes all of the elements from this list. The list will be empty after
	 * this call returns.
	 */
	public void clear()
	{
		// clear to let GC do its work
		for (int i = 0; i < size; i++)
			elementData[i] = null;

		size = 0;
	}

	public void sort()
	{
		// https://stackoverflow.com/questions/23362143/change-sort-order-of-strings-includes-with-a-special-character-e-g
		if (elementData == null || elementData.length == 0)
		{
			return;
		}
		quickSort(0, size - 1);
	}


	private void ensureCapacityInternal(int minCapacity)
	{
		if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
		{
			minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
		}

		// overflow-conscious code
		if (minCapacity - elementData.length > 0)
		{
			grow(minCapacity);
		}
	}

	/**
	 * Increases the capacity to ensure that it can hold at least the number of
	 * elements specified by the minimum capacity argument.
	 *
	 * @param minCapacity
	 *            the desired minimum capacity
	 */
	private void grow(int minCapacity)
	{
		// overflow-conscious code
		int oldCapacity = elementData.length;
		int newCapacity = oldCapacity + (oldCapacity >> 1);
		if (newCapacity - minCapacity < 0)
		{
			newCapacity = minCapacity;
		}
		if (newCapacity - MAX_ARRAY_SIZE > 0)
		{
			if (minCapacity < 0) // overflow
			{
				throw new OutOfMemoryError();
			}
			newCapacity = (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
		}
		// minCapacity is usually close to size, so this is a win:
		elementData = Arrays.copyOf(elementData, newCapacity);
	}
	
	private void quickSort(int lowerIndex, int higherIndex)
	{
		int i = lowerIndex;
		int j = higherIndex;
		TrieEntry temp = null;
		TrieEntry pivot = this.elementData[lowerIndex + (higherIndex - lowerIndex) / 2];

		while (i <= j)
		{
			while (this.elementData[i].getValue().compareToIgnoreCase(pivot.getValue()) < 0)
			{
				i++;
			}

			while (this.elementData[j].getValue().compareToIgnoreCase(pivot.getValue()) > 0)
			{
				j--;
			}

			if (i <= j)
			{
				temp = this.elementData[i];
				this.elementData[i] = this.elementData[j];
				this.elementData[j] = temp;
				temp = null;

				i++;
				j--;
			}
		}
		// call quickSort recursively
		if (lowerIndex < j)
		{
			quickSort(lowerIndex, j);
		}
		if (i < higherIndex)
		{
			quickSort(i, higherIndex);
		}
	}
}
