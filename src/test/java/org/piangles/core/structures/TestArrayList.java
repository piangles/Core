package org.piangles.core.structures;

import org.piangles.core.structures.StringArray;

public class TestArrayList
{
	public static void main(String[] args)
	{
		StringArray strings = new StringArray(10);
		System.out.println(strings.add("Hello"));
		System.out.println(strings.add("World"));
	}
}
