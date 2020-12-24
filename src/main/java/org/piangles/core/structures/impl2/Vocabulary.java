package org.piangles.core.structures.impl2;

public final class Vocabulary
{
	public static final String NULL_STR = "0000000000000000000000000000000000000000000000000000000000000000";

	//64 Bits
	public static final long NULL = 0b0000000000000000000000000000000000000000000000000000000000000000L;
	
	public static final long SPECIAL01 = 0b0000000000000000000000000000000000000000000000000000000000000001L;//!
	public static final long SPECIAL02 = 0b0000000000000000000000000000000000000000000000000000000000000010L;//"
	public static final long SPECIAL03 = 0b0000000000000000000000000000000000000000000000000000000000000100L;//#
	public static final long SPECIAL04 = 0b0000000000000000000000000000000000000000000000000000000000001000L;//$
	public static final long SPECIAL05 = 0b0000000000000000000000000000000000000000000000000000000000010000L;//%
	public static final long SPECIAL06 = 0b0000000000000000000000000000000000000000000000000000000000100000L;//&
	public static final long SPECIAL07 = 0b0000000000000000000000000000000000000000000000000000000001000000L;//'
	public static final long SPECIAL08 = 0b0000000000000000000000000000000000000000000000000000000010000000L;//(
	public static final long SPECIAL09 = 0b0000000000000000000000000000000000000000000000000000000100000000L;//)
	public static final long SPECIAL10 = 0b0000000000000000000000000000000000000000000000000000001000000000L;//*
	public static final long SPECIAL11 = 0b0000000000000000000000000000000000000000000000000000010000000000L;//+
	public static final long SPECIAL12 = 0b0000000000000000000000000000000000000000000000000000100000000000L;//,
	public static final long SPECIAL13 = 0b0000000000000000000000000000000000000000000000000001000000000000L;//-
	public static final long SPECIAL14 = 0b0000000000000000000000000000000000000000000000000010000000000000L;//.
	public static final long SPECIAL15 = 0b0000000000000000000000000000000000000000000000000100000000000000L;//Slash/
	
	public static final long NUMBER00 = 0b0000000000000000000000000000000000000000000000001000000000000000L;//0
	public static final long NUMBER01 = 0b0000000000000000000000000000000000000000000000010000000000000000L;//1
	public static final long NUMBER02 = 0b0000000000000000000000000000000000000000000000100000000000000000L;//2
	public static final long NUMBER03 = 0b0000000000000000000000000000000000000000000001000000000000000000L;//3
	public static final long NUMBER04 = 0b0000000000000000000000000000000000000000000010000000000000000000L;//4
	public static final long NUMBER05 = 0b0000000000000000000000000000000000000000000100000000000000000000L;//5
	public static final long NUMBER06 = 0b0000000000000000000000000000000000000000001000000000000000000000L;//6
	public static final long NUMBER07 = 0b0000000000000000000000000000000000000000010000000000000000000000L;//7
	public static final long NUMBER08 = 0b0000000000000000000000000000000000000000100000000000000000000000L;//8
	public static final long NUMBER09 = 0b0000000000000000000000000000000000000001000000000000000000000000L;//9
	
	public static final long SPECIAL16 = 0b0000000000001000000000000000000000000000000000000000000000000000L;//:
	public static final long SPECIAL17 = 0b0000000000010000000000000000000000000000000000000000000000000000L;//;
	public static final long SPECIAL18 = 0b0000000000100000000000000000000000000000000000000000000000000000L;//<
	public static final long SPECIAL19 = 0b0000000001000000000000000000000000000000000000000000000000000000L;//=
	public static final long SPECIAL20 = 0b0000000010000000000000000000000000000000000000000000000000000000L;//>
	public static final long SPECIAL21 = 0b0000000100000000000000000000000000000000000000000000000000000000L;//?
	public static final long SPECIAL22 = 0b0000001000000000000000000000000000000000000000000000000000000000L;//@
	
	public static final long A = 0b0000000000000000000000000000000000000010000000000000000000000000L;
	public static final long B = 0b0000000000000000000000000000000000000100000000000000000000000000L;
	public static final long C = 0b0000000000000000000000000000000000001000000000000000000000000000L;
	public static final long D = 0b0000000000000000000000000000000000010000000000000000000000000000L;
	public static final long E = 0b0000000000000000000000000000000000100000000000000000000000000000L;
	public static final long F = 0b0000000000000000000000000000000001000000000000000000000000000000L;
	public static final long G = 0b0000000000000000000000000000000010000000000000000000000000000000L;
	public static final long H = 0b0000000000000000000000000000000100000000000000000000000000000000L;
	public static final long I = 0b0000000000000000000000000000001000000000000000000000000000000000L;
	public static final long J = 0b0000000000000000000000000000010000000000000000000000000000000000L;
	
	public static final long K = 0b0000000000000000000000000000100000000000000000000000000000000000L;
	public static final long L = 0b0000000000000000000000000001000000000000000000000000000000000000L;
	public static final long M = 0b0000000000000000000000000010000000000000000000000000000000000000L;
	public static final long N = 0b0000000000000000000000000100000000000000000000000000000000000000L;
	public static final long O = 0b0000000000000000000000001000000000000000000000000000000000000000L;
	public static final long P = 0b0000000000000000000000010000000000000000000000000000000000000000L;
	public static final long Q = 0b0000000000000000000000100000000000000000000000000000000000000000L;
	public static final long R = 0b0000000000000000000001000000000000000000000000000000000000000000L;
	public static final long S = 0b0000000000000000000010000000000000000000000000000000000000000000L;
	public static final long T = 0b0000000000000000000100000000000000000000000000000000000000000000L;
	
	public static final long U = 0b0000000000000000001000000000000000000000000000000000000000000000L;
	public static final long V = 0b0000000000000000010000000000000000000000000000000000000000000000L;
	public static final long W = 0b0000000000000000100000000000000000000000000000000000000000000000L;
	public static final long X = 0b0000000000000001000000000000000000000000000000000000000000000000L;
	public static final long Y = 0b0000000000000010000000000000000000000000000000000000000000000000L;
	public static final long Z = 0b0000000000000100000000000000000000000000000000000000000000000000L;
	
	
	public static final long SPECIAL23 = 0b0000010000000000000000000000000000000000000000000000000000000000L;//[
	public static final long SPECIAL24 = 0b0000100000000000000000000000000000000000000000000000000000000000L;//\
	public static final long SPECIAL25 = 0b0001000000000000000000000000000000000000000000000000000000000000L;//]
	public static final long SPECIAL26 = 0b0010000000000000000000000000000000000000000000000000000000000000L;//^
	public static final long SPECIAL27 = 0b0100000000000000000000000000000000000000000000000000000000000000L;//_
	public static final long SPECIAL28 = 0b100000000000000000000000000000000000000000000000000000000000000L;//`
	
	public static final long SPECIAL29 = 0b0000000000000000000000000000000000000000000000000000000000000001L;//{
	public static final long SPECIAL30 = 0b0000000000000000000000000000000000000000000000000000000000000010L;//|
	public static final long SPECIAL31 = 0b0000000000000000000000000000000000000000000000000000000000000100L;//}
	public static final long SPECIAL32 = 0b0000000000000000000000000000000000000000000000000000000000001000L;//~
	
	private static long[] Group1List = null;
	private static long[] Group2List = null;

	static
	{
		Group1List = new long[]{ 
				SPECIAL01,SPECIAL02,SPECIAL03,SPECIAL04,SPECIAL05,SPECIAL06,SPECIAL07,SPECIAL08,SPECIAL09,SPECIAL10,
				SPECIAL11,SPECIAL12,SPECIAL13,SPECIAL14,SPECIAL15,
				NUMBER00, NUMBER01,NUMBER02,NUMBER03,NUMBER04,NUMBER05,NUMBER06,NUMBER07,NUMBER08,NUMBER09,
				SPECIAL16,SPECIAL17,SPECIAL18,SPECIAL19,SPECIAL20,SPECIAL21,SPECIAL22,
				A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
				SPECIAL23,SPECIAL24,SPECIAL25,SPECIAL26,SPECIAL27,SPECIAL28,
				};
		Group2List = new long[]{SPECIAL29,SPECIAL30,SPECIAL31,SPECIAL32};
	}
	
	public static CharacterDetails getCharacterDetails(char ch)//How to give if it is group 1 or 2
	{
		int ascii = (int)ch;
		int index = (ascii < 97)? (ascii - 33) : (ascii < 123)? (ascii-65) : (ascii-123);
		//System.out.println("For : " + ch + " ASCII is " + ascii + " is : " + index);
		return new CharacterDetails(ascii>122?1:2, index);
	}
	
	public static long getBinaryRepresentation(char ch)
	{
		int ascii = (int)ch;
		return (ascii < 123) ? Group1List[getCharacterDetails(ch).index] : Group2List[getCharacterDetails(ch).index];
	}
}