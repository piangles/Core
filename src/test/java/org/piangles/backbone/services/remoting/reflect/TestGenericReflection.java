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
 
 
 
package org.piangles.backbone.services.remoting.reflect;

import java.lang.reflect.Method;

import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamProcessor;
import org.piangles.core.util.reflect.TypeResolver;

public class TestGenericReflection
{
	public static void main(String[] args)
	{
		StreamProcessor<SomeClass, Object> processor = (obj) -> {return null;};  

		for (Method method : processor.getClass().getMethods())
		{
			if (method.getName().equals("process"))
			{
				Class<?>[] typeArgs = TypeResolver.resolveRawArguments(StreamProcessor.class, processor.getClass());
				System.out.println(typeArgs[0]);
				System.out.println(method);
			}
		}
		
		for (Method method : SomeInterface.class.getMethods())
		{
			System.out.println(method.getReturnType().getCanonicalName());
		}
	}
	
	public class SomeClass
	{

	}
	
	public interface SomeInterface
	{
		Stream<String> getValues();
	}
}
