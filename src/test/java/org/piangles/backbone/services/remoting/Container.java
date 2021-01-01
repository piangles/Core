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
