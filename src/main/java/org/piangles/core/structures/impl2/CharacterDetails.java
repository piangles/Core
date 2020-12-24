package org.piangles.core.structures.impl2;

final class CharacterDetails
{
	int groupNo;
	int index;
	long binaryValue;

	CharacterDetails(long binaryValue)
	{
		this.binaryValue = binaryValue;
	}

	CharacterDetails(int groupNo, int index)
	{
		this.groupNo = groupNo;
		this.index = index;
	}
}
