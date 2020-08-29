package com.TBD.backbone.services.remoting.rabbit;

public class ArrayCopyTest
{

	public static void main(String[] cmdArgs)
	{
		Object[] args = new Object[]{"A", "B", "C"};
		Object[] modifiedArgs = new Object[1 + args.length];
		modifiedArgs[0] = "Test";
		System.arraycopy(args, 0, modifiedArgs, 1, args.length);
		for (Object object : modifiedArgs)
		{
			System.out.println(object);
		}
	}

}
