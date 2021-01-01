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
