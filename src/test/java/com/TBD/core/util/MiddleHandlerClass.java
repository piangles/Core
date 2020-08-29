package com.TBD.core.util;

import com.TBD.core.util.ClassHelper;

public class MiddleHandlerClass
{
	public void doSomething()
	{
		ClassHelper classHelper = new ClassHelper();
		System.out.println(classHelper.getClassName() + " : " + classHelper.getLineNumber());
		System.out.println(classHelper.getCompleteStackTrace());
	}
}
