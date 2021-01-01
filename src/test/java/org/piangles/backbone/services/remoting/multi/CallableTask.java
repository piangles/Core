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
