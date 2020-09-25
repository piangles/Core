package org.piangles.core.dao.rdbms;

import org.piangles.core.resources.RDBMSDataStore;

public class TestDataStore
{
	public static void main(String[] args)
	{
		System.out.println(RDBMSDataStore.createCALLString("TEST_SP", 0));
		System.out.println(RDBMSDataStore.createCALLString("TEST_SP", 1));
		System.out.println(RDBMSDataStore.createCALLString("TEST_SP", 2));
	}
}
