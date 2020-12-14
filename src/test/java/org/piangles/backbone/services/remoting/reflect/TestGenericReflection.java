package org.piangles.backbone.services.remoting.reflect;

import java.lang.reflect.Method;

import org.piangles.core.stream.Processor;
import org.piangles.core.util.reflect.TypeResolver;

public class TestGenericReflection
{
	public static void main(String[] args)
	{
		Processor<SomeClass> processor = (obj) -> {};  

		for (Method method : processor.getClass().getMethods())
		{
			if (method.getName().equals("process"))
			{
				Class<?>[] typeArgs = TypeResolver.resolveRawArguments(Processor.class, processor.getClass());
				System.out.println(typeArgs[0]);
				System.out.println(method);
			}
		}
	}
	
	public class SomeClass
	{

	}
}
