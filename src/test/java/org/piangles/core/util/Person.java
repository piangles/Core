package org.piangles.core.util;

import java.io.Serializable;

public class Person implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private int age;
	private boolean citizen;
	
	public Person(String name, int age, boolean citizen)
	{
		this.name = name;
		this.age = age;
		this.citizen = citizen;
	}
	public String getName()
	{
		return name;
	}
	public int getAge()
	{
		return age;
	}
	public boolean isCitizen()
	{
		return citizen;
	}
	@Override
	public String toString()
	{
		return "Person [name=" + name + ", age=" + age + ", citizen=" + citizen + "]";
	}
}
