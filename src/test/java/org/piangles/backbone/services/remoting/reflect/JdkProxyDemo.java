package org.piangles.backbone.services.remoting.reflect;

import java.lang.reflect.Proxy;

public class JdkProxyDemo
{
	public static void main(String[] args)
	{
		Handler handler = new Handler(new OriginalImpl());
		MyInterface f = (MyInterface) Proxy.newProxyInstance(MyInterface.class.getClassLoader(), new Class[] { MyInterface.class }, handler);
		//System.out.println("What is this : " + f);
		f.originalMethod("Hallo");
	}

}