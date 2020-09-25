package org.piangles.core.util;

public enum Outcome
{
	Success(true),
	Fail(false);
	
	private boolean result = false;
	
	private Outcome(boolean result)
	{
		this.result = result;
	}
	
	public final static Outcome getValue(boolean result)
	{
		Outcome value = null;
		for (Outcome outcome : Outcome.values())
		{
			if (outcome.result == result)
			{
				value = outcome;
				break;
			}
		}
		return value;
	}
}
