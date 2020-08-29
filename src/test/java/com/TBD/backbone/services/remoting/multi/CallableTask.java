package com.TBD.backbone.services.remoting.multi;

import java.util.concurrent.Callable;

public class CallableTask implements Callable<CalcResult>
{
	String taskName;
	long input1;
	int input2;

	CallableTask(String name, long v1, int v2)
	{
		taskName = name;
		input1 = v1;
		input2 = v2;
	}

	public CalcResult call() throws Exception
	{
		System.out.println(" Task " + taskName + " Started -----");
		for (int i = 0; i < input2; i++)
		{
			try
			{
				Thread.sleep(200);
			}
			catch (InterruptedException e)
			{
				System.out.println(" Task " + taskName + " Interrupted !! ");
				e.printStackTrace();
			}
			input1 += i;
		}
		System.out.println(" Task " + taskName + " Completed @@@@@@");
		return new CalcResult(input1);
	}

}