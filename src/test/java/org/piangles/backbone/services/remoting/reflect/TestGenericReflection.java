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
