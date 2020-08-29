package com.TBD.core.resources;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

import com.TBD.core.util.abstractions.Decrypter;

/**
 * As the name suggests it is a wrapper for creating connection pool.
 *
 */
public class RDBMSDataStore
{
	public static final String DRIVER_CLASSNAME = "DriverClassName";
	public static final String LOGIN_ID = "LoginId";
	public static final String PASSWORD = "Password";
	public static final String URL = "URL";
	private static final String DECRYPTER_CLASS_NAME = "DecrypterClassName";
	private static final String DECRYPTER_AUTHZ_ID = "DecrypterAuthorizationId";

	private static final String SP_BEGIN = "{call ";
	private static final String SP_PARAM = "?";
	private static final String SP_END = ")}";

	private BasicDataSource ds = null;

	RDBMSDataStore(Properties dataStoreProps) throws Exception
	{
		String decrypterClassName = dataStoreProps.getProperty(DECRYPTER_CLASS_NAME);
		String decrypterAuthorizationId = dataStoreProps.getProperty(DECRYPTER_AUTHZ_ID);
		Decrypter decrypter = (Decrypter)Class.forName(decrypterClassName).getConstructor(String.class).newInstance(decrypterAuthorizationId);
		
		ds = new BasicDataSource();
		ds.setDriverClassName(dataStoreProps.getProperty(DRIVER_CLASSNAME));
		ds.setUsername(decrypter.decrypt(dataStoreProps.getProperty(LOGIN_ID)));
		ds.setPassword(decrypter.decrypt(dataStoreProps.getProperty(PASSWORD)));
		ds.setUrl(dataStoreProps.getProperty(URL));

		// the settings below are optional -- dbcp can work with
		// defaults
		ds.setMinIdle(5);
		ds.setMaxIdle(20);
		ds.setMaxOpenPreparedStatements(180);
	}

	public Connection getConnection() throws SQLException
	{
		return this.ds.getConnection();
	}

	public static String createCALLString(String storedProcName, int paramCount)
	{
		StringBuffer sb = new StringBuffer(SP_BEGIN);
		sb.append(storedProcName).append("(");

		for (int i = 0; i < paramCount; ++i)
		{
			if (i != 0)
			{
				sb.append(",");
			}
			sb.append(SP_PARAM);
		}

		sb.append(SP_END);
		return sb.toString();
	}
}