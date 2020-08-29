package com.TBD.backbone.services.remoting.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Handler implements InvocationHandler
{
	private MyInterface original;

	public Handler(MyInterface original)
	{
		this.original = original;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		System.out.println("Original : " + original);
		System.out.println("Original : " + method.getName());
		System.out.println("Proxy : " + proxy);
		
		System.out.println("BEFORE");
		method.invoke(original, args);
		System.out.println("AFTER");
		return null;
	}
}