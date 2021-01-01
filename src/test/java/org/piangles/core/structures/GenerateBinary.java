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
 
 
 
package org.piangles.core.structures;

import java.math.BigInteger;

public class GenerateBinary
{

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		//System.out.println(4611686018427387904L * 2);//-9223372036854775808
		long start = 4611686018427387904L;
		for (int i =63; i > -1; --i)
		{
			//System.out.println( Math.pow(2, i));
			String bin = Long.toBinaryString(start / (long)Math.pow(2, i));
			//System.out.println(bin);
			String formatted = String.format("%64s", bin).replaceAll(" ", "0");
			System.out.println("public static final long A = 0b" + formatted + "L;");
		}
	}

}
