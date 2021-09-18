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
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class NADReader
{
	public static void main(String[] args) throws IOException
	{
		/**
No Of lines : 53,595,066 SkippedCount0 TimeTaken: 46,543 MilliSeconds.
OID,State,County,Inc_Muni,Uninc_Comm,Nbrhd_Comm,Post_Comm,Zip_Code,Plus_4,Bulk_Zip,Bulk_Plus4,StN_PreMod,StN_PreDir,StN_PreTyp,StN_PreSep,StreetName,StN_PosTyp,StN_PosDir,StN_PosMod,AddNum_Pre,Add_Number,AddNum_Suf,LandmkPart,LandmkName,Building,Floor,Unit,Room,Addtl_Loc,Milepost,Longitude,Latitude,NatGrid_Coord,GUID,Addr_Type,Placement,Source,AddAuth,UniqWithin,LastUpdate,Effective,Expired
-1,AR,Arkansas,,,,De Witt,72042,3714,,,,,,,Molly,Lane,,,,27,,,,,,,,,,-91.249442419999994,34.187814209999999,15SXT6131284365,{5209A942-A47E-4B3A-BC7E-90E3F597A65A},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,0000,,,,East,,,10th,Street,,,,1300,,,,,,,,,,-91.537553180000003,34.493373669999997,15SXU3427317833,{D3BB340C-D14D-4D21-A391-F4C67E87B79D},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,5502,,,,East,,,10th,,,,,812,,,,,,,,,,-91.543237570000002,34.493516900000003,15SXU3375117841,{BF651139-99E6-4751-98FC-3EF1914AC936},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,5313,,,,East,,,10th,Street,,,,403,,,,,,,,,,-91.548709830000007,34.494039409999999,15SXU3324717892,{168963F3-7A57-4985-A285-DBA1030C8B3A},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,5404,,,,East,,,10th,Street,,,,604,,,,,,,,,,-91.546327180000006,34.493649339999997,15SXU3346717852,{8348E6D4-A50D-4488-AB6D-263E4143523C},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,5401,,,,East,,,10th,Street,,,,501,,,,,,,,,,-91.547623970000004,34.494033450000003,15SXU3334717892,{5F398FCD-E08F-417C-B194-B65F52DF51CB},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,5405,,,,East,,,10th,,,,,707,,,,,,,,,,-91.544675979999994,34.494029970000000,15SXU3361817896,{6140A757-79C3-4F1D-969B-DF36EFC744B6},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,5313,,,,East,,,10th,Street,,,,405,,,,,,,,,,-91.548510710000002,34.494110280000001,15SXU3326517900,{BBDD75E1-31C6-42EB-9A17-68B3A15C77C3},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,5402,,,,East,,,10th,Street,,,,502,,,,,,,,,,-91.547642080000003,34.493624349999997,15SXU3334617847,{492E0265-A38E-438C-9A00-5E85EF93D4D1},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,5402,,,,East,,,10th,Street,,,,504,,,,,,,,,,-91.547465209999999,34.493661860000003,15SXU3336217851,{A4DA29B2-A926-4DC7-A35E-50D5E4361A00},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,5402,,,,East,,,10th,,,,,512,,,,,,,,,,-91.546799300000004,34.493535639999997,15SXU3342417838,{C665E925-ED4B-4ED8-817B-9CAAE639D44E},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,5403,,,,East,,,10th,Street,,,,607,,,,,,,,,,-91.546009440000006,34.494010400000001,15SXU3349517892,{1C734290-584E-4955-AA00-53EE81D25C61},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,5401,,,,East,,,10th,Street,,,,505,,,,,,,,,,-91.547391570000002,34.494032689999997,15SXU3336817893,{D568F3B1-9110-45E0-BBD2-742FDF79C6D1},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,5314,,,,East,,,10th,Street,,,,410,,,,,,,,,,-91.548021180000006,34.493584360000000,15SXU3331117842,{0AEC8388-52C5-4ADA-AC12-8E014DBFE3AA},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
-1,AR,Arkansas,Stuttgart,,,Stuttgart,72160,5402,,,,East,,,10th,Street,,,,506,,,,,,,,,,-91.547308360000002,34.493623130000003,15SXU3337717847,{8B89C155-388D-4120-842A-38B79C9FB71D},Residential,Unknown,Arkansas Geographic Information Office,001,,2/1/2017 0:00:00,,
		 */
		boolean[] useColumns = new boolean[100];
		useColumns[1] = true; 
		useColumns[2] = true;
		useColumns[3] = true;
		useColumns[6] = true;
		useColumns[7] = true;
		useColumns[8] = true;
		useColumns[12] = true;
		useColumns[15] = true;
		useColumns[16] = true;
		useColumns[20] = true;
		useColumns[30] = true;
		useColumns[31] = true;
		useColumns[34] = true;
		useColumns[36] = true;
		useColumns[39] = true;
		
		FileInputStream fis = new FileInputStream(new File("C:\\Users\\sarad\\git\\Export\\NAD_r4.txt"));
		InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
		BufferedReader br = new BufferedReader(isr);

		String actualLine = null;
		String deaccentedLine = null;
		int currentLineNo = 0;
		final PrintWriter writer = new PrintWriter("C:\\Users\\sarad\\git\\Export\\SubSet.txt", "UTF-8");
		//final PrintWriter writer = new PrintWriter(System.out);
		long startTime = 0L;
		int count = 0;
		while ((actualLine = br.readLine()) != null)
		{
			count++;
			if (count % 100000 == 0) System.out.println(count);
			try
			{
				String[] columns = actualLine.split(",");

				for (int i=0; i < columns.length; ++i)
				{
					if (useColumns[i])
					{
						if (i != 1)
						{
							writer.print(",");
						}
						writer.print(columns[i]);
					}
					if (columns[0].equalsIgnoreCase("il"))
					{
						System.out.println(actualLine);
					}
				}
				writer.println();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
