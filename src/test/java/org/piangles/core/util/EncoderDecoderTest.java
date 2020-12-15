package org.piangles.core.util;

import org.piangles.core.stream.Streamlet;
import org.piangles.core.util.coding.JAVA;

import com.google.gson.reflect.TypeToken;

public class EncoderDecoderTest
{
	public static void main(String[] args) throws Exception
	{
		Person p = new Person("Name", 16, true);
		Streamlet<Person> st = new Streamlet<>(p);
		byte[] codedBytes = null;
		
		codedBytes = JAVA.getEncoder().encode(st);
		
		System.out.println(new String(codedBytes));
		st = JAVA.getDecoder().decode(codedBytes, new TypeToken<Streamlet<Person>>() {}.getType());
		System.out.println(st);
	}
}
