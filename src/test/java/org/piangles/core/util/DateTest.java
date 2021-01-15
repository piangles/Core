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
 
 
package org.piangles.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTest
{
	public static void main(String[] args)
	{
		Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyy HH:mm:ss.SSS");

		System.out.println(TimeZone.getDefault().getDisplayName());

		System.out.println(currentDate.toString());
		System.out.println(sdf.format(currentDate));
		
		System.out.println("Changing TimeZone...");
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		System.out.println(currentDate.toString());
		System.out.println(sdf.format(currentDate));
	}
}
