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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.NumberFormat;

public final class UTF8FileReader
{
	private static CharsetEncoder encoder = Charset.forName("US-ASCII").newEncoder();
	private static NumberFormat nf = NumberFormat.getNumberInstance();
	private static final int BUFFER_SIZE = 8192;
	private static final int LINE_SEPARATOR_LENGTH = System.lineSeparator().length();
	
	private File file = null;
	private int noOfLines = 0;
	private int noOfLinesSkipped = 0;	
	private int longestLine = 0;
	private int noOfLinesWithSpecialCharacters = 0;
	
	private boolean deaccent = false;
	private LineProcessor lp = null;

	public UTF8FileReader(String pathToFile, boolean deaccent, LineProcessor lp) throws IOException
	{
		this(new File(pathToFile), deaccent, lp);
	}

	public UTF8FileReader(File file, boolean deaccent, LineProcessor lp) throws IOException
	{
		this.file = file;
		this.deaccent = deaccent;
		this.lp = lp;
		gatherFileStats();
	}
	
	public int getNoOfLines()
	{
		return noOfLines;
	}
	
	public int getNoOfLinesSkipped()
	{
		return noOfLinesSkipped;
	}

	public void processFile() throws IOException
	{
		long startTime = System.currentTimeMillis();
		processFileTraditional();
		//processFileProprietary();
		Logger.getInstance().info("ProcessFile NoOfLines: " + nf.format(noOfLines) +
							"\nNoOfLinesWithSpecialCharacters: " + nf.format(noOfLinesWithSpecialCharacters) +
							"\nTimeTaken: " + nf.format(System.currentTimeMillis() - startTime) + " MilliSeconds.");
	}

	private void gatherFileStats() throws IOException
	{
		long startTime = System.currentTimeMillis();
		FileInputStream stream = new FileInputStream(file);
		byte[] buffer = new byte[BUFFER_SIZE];
		
		noOfLines = 0;
		
		int noOfBytesRead;
		int currentWordLength = 0;
		while ((noOfBytesRead = stream.read(buffer)) > 0)
		{
			for (int i = 0; i < noOfBytesRead; i++)
			{
				currentWordLength++;
				
				if (buffer[i] == '\n')
				{
					noOfLines++;
					if (currentWordLength > longestLine)
					{
						longestLine = currentWordLength;
					}
					currentWordLength = 0;
				}
			}
		}
		stream.close();
		Logger.getInstance().info("No Of lines : " + nf.format(noOfLines) + " SkippedCount" + nf.format(noOfLinesSkipped) + " TimeTaken: " + nf.format(System.currentTimeMillis() - startTime) + " MilliSeconds.");
	}

	private void processFileTraditional() throws IOException
	{
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
		BufferedReader br = new BufferedReader(isr);

		String actualLine = null;
		String deaccentedLine = null;
		int currentLineNo = 0;
		while ((actualLine = br.readLine()) != null)
		{
			currentLineNo++;
			actualLine = actualLine.trim();
			if (deaccent)
			{
				deaccentedLine = deaccent(actualLine);
			}
			boolean result = lp.process(actualLine, deaccentedLine, currentLineNo, (int)((100 * currentLineNo) / noOfLines));
			if (!result)
			{
				noOfLinesSkipped++;
			}
		}
		br.close();
	}
	
	private void processFileProprietary() throws IOException
	{
		byte[] buffer = new byte[BUFFER_SIZE];
		byte[] lineBuffer = new byte[longestLine];
		
		FileInputStream stream = new FileInputStream(file);
		int noOfBytesRead = 0;
		int lineIndex = 0;
		int bufferIndex = 0;
		int lineLen = 0;
		int currentLineNo = 0;
		String actualLine = null;
		String deaccentedLine = null;
		while ((noOfBytesRead = stream.read(buffer)) > 0)
		{
			bufferIndex = 0;
			for (int i = 0; i < noOfBytesRead; ++i)
			{
				lineLen = lineLen + 1;
				if ((int)buffer[i] == '\n')
				{
					currentLineNo++;
					System.arraycopy(buffer, bufferIndex, lineBuffer, lineIndex, lineLen-lineIndex);

					
					actualLine = new String(lineBuffer, 0, lineLen-LINE_SEPARATOR_LENGTH, StandardCharsets.UTF_8);
					actualLine = actualLine.trim();
					if (deaccent)
					{
						deaccentedLine = deaccent(actualLine);
					}
					boolean result = lp.process(actualLine, deaccentedLine, currentLineNo, (int)((100 * currentLineNo) / noOfLines));
					if (!result)
					{
						noOfLinesSkipped++;
					}
					
					//Reset
					bufferIndex = i+1;
					lineIndex = 0;
					lineLen = 0;
				}
			}
			if (lineLen != 0)
			{
				System.arraycopy(buffer, bufferIndex, lineBuffer, lineIndex, lineLen-lineIndex);
				
				lineIndex = lineLen;
				bufferIndex = 0;
			}
		}
		stream.close();
	}

	private String deaccent(String actualLine)
	{
		String deccentedLine = actualLine;

		if (!encoder.canEncode(actualLine))//Is actualLine having special characters
		{
			noOfLinesWithSpecialCharacters++;
			deccentedLine = Normalizer.normalize(actualLine, Normalizer.Form.NFD);
			deccentedLine = deccentedLine.replaceAll("[^\\p{ASCII}]", "");
		}
		
		return deccentedLine; 
	}
	
//	public static void main(String[] args) throws Exception
//	{
//		String[] lines = new String[] {
//			"Hello World",
//			"Die große Fahrt",
//			"¿Quién controla la tienda?",
//			"Agent 8¾",
//			"Nec'ro·man'cy",
//			"¡Alambrista! - The Illegal",
//			"Exposé, My Lovely"			
//		};
//		
//		for (String line : lines)
//		{
//			Logger.getInstance().info("Actual:" + line + " ===== DeAccented:" + deaccent(line));
//		}
//	}
}
