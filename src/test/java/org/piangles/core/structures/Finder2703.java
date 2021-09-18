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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

public class Finder2703
{
	public static void main(String[] args) throws IOException
	{
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\sarad\\git\\Export\\NAD_r4.txt"));
		InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
		BufferedReader br = new BufferedReader(isr);

		String actualLine = null;
		String state = "";
		Set<String> states = new LinkedHashSet<>();
		while ((actualLine = br.readLine()) != null)
		{
			String[] columns = actualLine.split(",");
			try
			{
				//https://bedes.lbl.gov/bedes-online/street-name-post-type
				//http://technet.nena.org/nrs/registry/StreetNamePreTypeSeparators.xml
//				if (!state.equals(columns[0]))
//				{
//					if (columns[0].trim().length() == 0)
//					{
//						System.out.println(actualLine);
//					}
//					else if (columns[0] != null)
//					{
//						state = columns[0];
//					}
//					else
//					{
//						state = "Empty";
//					}
//					states.add(state);
//					System.out.println("[" + state + "]");
//				}
				if (	columns[1] != null && columns[1].equals("TX") && 
						//columns[2] != null && columns[2].equalsIgnoreCase("middlesex") &&
						columns[15] != null && columns[15].equalsIgnoreCase("camellia") &&
						columns[16] != null && columns[16].equalsIgnoreCase("grove")
					)
				//if (columns[1] != null && columns[1].equalsIgnoreCase("IL"))
				{
					System.out.println(actualLine);
				}
			}
			catch (Exception e)
			{
				System.out.println(actualLine);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(states);
	}
}
