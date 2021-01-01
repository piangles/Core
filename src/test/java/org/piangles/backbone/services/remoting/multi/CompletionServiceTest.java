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
 
 
 
package org.piangles.backbone.services.remoting.multi;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CompletionServiceTest
{
	public static void main(String[] args)
	{
		ExecutorService taskExecutor = Executors.newFixedThreadPool(3);
		CompletionService<CalcResult> taskCompletionService = new ExecutorCompletionService<CalcResult>(taskExecutor);

		int submittedTasks = 5;
		for (int i = 0; i < submittedTasks; i++)
		{
			taskCompletionService.submit(new CallableTask(String.valueOf(i), (i * 10), ((i * 10) + 10)));
			System.out.println("Task " + String.valueOf(i) + "subitted");
		}
		
		for (int tasksHandled = 0; tasksHandled < submittedTasks; tasksHandled++)
		{
			try
			{
				System.out.println("trying to take from Completion service");
				Future<CalcResult> result = taskCompletionService.take();
				System.out.println("result for a task availble in queue.Trying to get()");
				// above call blocks till atleast one task is completed and
				// results availble for it
				// but we dont have to worry which one

				// process the result here by doing result.get()
				CalcResult l = result.get();
				System.out.println("Task " + String.valueOf(tasksHandled) + "Completed - results obtained : " + String.valueOf(l.result));

			}
			catch (InterruptedException e)
			{
				// Something went wrong with a task submitted
				System.out.println("Error Interrupted exception");
				e.printStackTrace();
			}
			catch (ExecutionException e)
			{
				// Something went wrong with the result
				e.printStackTrace();
				System.out.println("Error get() threw exception");
			}
		}
		System.exit(1);
	}
}
