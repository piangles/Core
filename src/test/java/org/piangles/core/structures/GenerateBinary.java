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
