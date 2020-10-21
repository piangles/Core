package org.piangles.backbone.services.remoting;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Container
{
	private static int count = 0;

	public static void main(String[] args) throws Exception
	{
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(new MyThreadFactory());

		Runnable task1 = () -> {
			new Exception().printStackTrace();
			count++;
			System.out.println("Running...task1 - count : " + count);
		};

		// init Delay = 5, repeat the task every 1 second
		ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(task1, 5, 1, TimeUnit.SECONDS);

		while (true)
		{
			System.out.println("count :" + count);
			Thread.sleep(1000);
			if (count == 5)
			{
				System.out.println("Count is 5, cancel the scheduledFuture!");
				scheduledFuture.cancel(true);
				ses.shutdown();
				break;
			}
		}
	}
}
