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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DefaultVocabulary implements Vocabulary
{
	private static final long serialVersionUID = 1L;
	
	//64 Bits
	private static final long A = 0b0000000000000000000000000000000000000000000000000000000000000001L;
	private static final long B = 0b0000000000000000000000000000000000000000000000000000000000000010L;
	private static final long C = 0b0000000000000000000000000000000000000000000000000000000000000100L;
	private static final long D = 0b0000000000000000000000000000000000000000000000000000000000001000L;
	private static final long E = 0b0000000000000000000000000000000000000000000000000000000000010000L;
	private static final long F = 0b0000000000000000000000000000000000000000000000000000000000100000L;
	private static final long G = 0b0000000000000000000000000000000000000000000000000000000001000000L;
	private static final long H = 0b0000000000000000000000000000000000000000000000000000000010000000L;
	private static final long I = 0b0000000000000000000000000000000000000000000000000000000100000000L;
	private static final long J = 0b0000000000000000000000000000000000000000000000000000001000000000L;
	private static final long K = 0b0000000000000000000000000000000000000000000000000000010000000000L;
	private static final long L = 0b0000000000000000000000000000000000000000000000000000100000000000L;
	private static final long M = 0b0000000000000000000000000000000000000000000000000001000000000000L;
	private static final long N = 0b0000000000000000000000000000000000000000000000000010000000000000L;
	private static final long O = 0b0000000000000000000000000000000000000000000000000100000000000000L;
	private static final long P = 0b0000000000000000000000000000000000000000000000001000000000000000L;
	private static final long Q = 0b0000000000000000000000000000000000000000000000010000000000000000L;
	private static final long R = 0b0000000000000000000000000000000000000000000000100000000000000000L;
	private static final long S = 0b0000000000000000000000000000000000000000000001000000000000000000L;
	private static final long T = 0b0000000000000000000000000000000000000000000010000000000000000000L;
	private static final long U = 0b0000000000000000000000000000000000000000000100000000000000000000L;
	private static final long V = 0b0000000000000000000000000000000000000000001000000000000000000000L;
	private static final long W = 0b0000000000000000000000000000000000000000010000000000000000000000L;
	private static final long X = 0b0000000000000000000000000000000000000000100000000000000000000000L;
	private static final long Y = 0b0000000000000000000000000000000000000001000000000000000000000000L;
	private static final long Z = 0b0000000000000000000000000000000000000010000000000000000000000000L;
	
	private static final long NUMBER00 = 0b0000000000000000000000000000000000000100000000000000000000000000L;
	private static final long NUMBER01 = 0b0000000000000000000000000000000000001000000000000000000000000000L;
	private static final long NUMBER02 = 0b0000000000000000000000000000000000010000000000000000000000000000L;
	private static final long NUMBER03 = 0b0000000000000000000000000000000000100000000000000000000000000000L;
	private static final long NUMBER04 = 0b0000000000000000000000000000000001000000000000000000000000000000L;
	private static final long NUMBER05 = 0b0000000000000000000000000000000010000000000000000000000000000000L;
	private static final long NUMBER06 = 0b0000000000000000000000000000000100000000000000000000000000000000L;
	private static final long NUMBER07 = 0b0000000000000000000000000000001000000000000000000000000000000000L;
	private static final long NUMBER08 = 0b0000000000000000000000000000010000000000000000000000000000000000L;
	private static final long NUMBER09 = 0b0000000000000000000000000000100000000000000000000000000000000000L;
	
	private static final long SPECIAL01 = 0b0000000000000000000000000001000000000000000000000000000000000000L;//SPACE
	private static final long SPECIAL02 = 0b0000000000000000000000000010000000000000000000000000000000000000L;//#
	private static final long SPECIAL03 = 0b0000000000000000000000000100000000000000000000000000000000000000L;//$
	private static final long SPECIAL04 = 0b0000000000000000000000001000000000000000000000000000000000000000L;//&
	private static final long SPECIAL05 = 0b0000000000000000000000010000000000000000000000000000000000000000L;//(
	private static final long SPECIAL06 = 0b0000000000000000000000100000000000000000000000000000000000000000L;//)
	private static final long SPECIAL07 = 0b0000000000000000000001000000000000000000000000000000000000000000L;//*
	private static final long SPECIAL08 = 0b0000000000000000000010000000000000000000000000000000000000000000L;//+
	private static final long SPECIAL09 = 0b0000000000000000000100000000000000000000000000000000000000000000L;//-
	private static final long SPECIAL10 = 0b0000000000000000001000000000000000000000000000000000000000000000L;//.
	private static final long SPECIAL11 = 0b0000000000000000010000000000000000000000000000000000000000000000L;//Forward /
	private static final long SPECIAL12 = 0b0000000000000000100000000000000000000000000000000000000000000000L;//=
	private static final long SPECIAL13 = 0b0000000000000001000000000000000000000000000000000000000000000000L;//@
	private static final long SPECIAL14 = 0b0000000000000010000000000000000000000000000000000000000000000000L;//^
	private static final long SPECIAL15 = 0b0000000000000100000000000000000000000000000000000000000000000000L;//_
	
//	private static final long A = 0b0000000000001000000000000000000000000000000000000000000000000000L;
//	private static final long A = 0b0000000000010000000000000000000000000000000000000000000000000000L;
//	private static final long A = 0b0000000000100000000000000000000000000000000000000000000000000000L;
//	private static final long A = 0b0000000001000000000000000000000000000000000000000000000000000000L;
//	private static final long A = 0b0000000010000000000000000000000000000000000000000000000000000000L;
//	private static final long A = 0b0000000100000000000000000000000000000000000000000000000000000000L;
//	private static final long A = 0b0000001000000000000000000000000000000000000000000000000000000000L;
//	private static final long A = 0b0000010000000000000000000000000000000000000000000000000000000000L;
//	private static final long A = 0b0000100000000000000000000000000000000000000000000000000000000000L;
//	private static final long A = 0b0001000000000000000000000000000000000000000000000000000000000000L;
//	private static final long A = 0b0010000000000000000000000000000000000000000000000000000000000000L;
//	private static final long A = 0b0100000000000000000000000000000000000000000000000000000000000000L;

	public static final String STOP_WORDS = "of,the,is,and,on,a,for,at";
	
	private static long[] list = null;
	private static int[] indexLookupMap = new int[128];
	private List<String> stopWords = new ArrayList<String>();
	
	static //Initialize 
	{
		list = new long[]{
				//Alphabets : 0-25
				A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
				//Numbers : 26-35
				NUMBER00, NUMBER01,NUMBER02,NUMBER03,NUMBER04,NUMBER05,NUMBER06,NUMBER07,NUMBER08,NUMBER09,
				//Limited Special : 36-49
				SPECIAL01,SPECIAL02,SPECIAL03,SPECIAL04,SPECIAL05,SPECIAL06,SPECIAL07,SPECIAL08,SPECIAL09,SPECIAL10,
				SPECIAL11,SPECIAL12,SPECIAL13,SPECIAL14,SPECIAL15
				};
		
		Arrays.fill(indexLookupMap, -1);
		
		int index = 0;
		for (int i='a'; i <= 'z'; ++i)
		{
			indexLookupMap[i] = index++;
		}
		for (int i=48; i <= 57; ++i)
		{
			indexLookupMap[i] = index++;
		}
		indexLookupMap[' '] = 36;
		indexLookupMap['#'] = 37;
		indexLookupMap['$'] = 38;
		indexLookupMap['&'] = 39;
		indexLookupMap['('] = 40;
		indexLookupMap[')'] = 41;
		indexLookupMap['*'] = 42;
		indexLookupMap['+'] = 43;
		indexLookupMap['-'] = 44;
		indexLookupMap['.'] = 45;
		indexLookupMap['/'] = 46;
		indexLookupMap['='] = 47;
		indexLookupMap['@'] = 48;
		indexLookupMap['^'] = 49;
		indexLookupMap['_'] = 50;
		
	}
	
	public DefaultVocabulary()
	{
		this(Arrays.asList(STOP_WORDS.split(",")));
	}

	public DefaultVocabulary(List<String> stopWords)
	{
		this.stopWords = stopWords;
	}

	@Override
	public boolean exists(char ch)
	{
		return getIndex(ch) != -1;
	}

	@Override
	public int getIndex(char ch)
	{
		int index = -1;
		
		if (indexLookupMap.length > (int)ch)
		{
			index = indexLookupMap[(int)ch];
		}
		
		return index;
	}
	
	@Override
	public long getBinaryRepresentation(char ch)
	{
		return list[getIndex(ch)];
	}

	@Override
	public List<String> getStopWords()
	{
		return stopWords;
	}
}
