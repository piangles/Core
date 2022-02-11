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
 
 
 
package org.piangles.core.resources;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.piangles.core.util.Logger;
import org.piangles.core.util.abstractions.Decrypter;

/**
 * As the name suggests it is a wrapper for creating connection pool.
 *
 */
public final class RDBMSDataStore implements Resource
{
	private static final String DRIVER_CLASSNAME = "DriverClassName";
	private static final String LOGIN_ID = "LoginId";
	private static final String PASSWORD = "Password";
	private static final String URL = "URL";
	private static final String DECRYPTER_CLASS_NAME = "DecrypterClassName";
	private static final String DECRYPTER_AUTHZ_ID = "DecrypterAuthorizationId";

	private static final String SP_BEGIN = "call ";
	private static final String SP_PARAM = "?";
	private static final String SP_END = ")";

	private static final String FUNC_BEGIN = "{call ";
	private static final String FUNC_PARAM = "?";
	private static final String FUNC_END = ")}";

	private BasicDataSource ds = null;
	
	private String serviceName = null;

	RDBMSDataStore(String serviceName, Properties dataStoreProps) throws Exception
	{
		this.serviceName = serviceName;
		
		String decrypterClassName = dataStoreProps.getProperty(DECRYPTER_CLASS_NAME);
		String decrypterAuthorizationId = dataStoreProps.getProperty(DECRYPTER_AUTHZ_ID);
		
		Decrypter decrypter = (Decrypter)Class.forName(decrypterClassName).getConstructor().newInstance();
		decrypter.init(serviceName, "Configuration", DECRYPTER_AUTHZ_ID, decrypterAuthorizationId);
		
		ds = new BasicDataSource();
		ds.setDriverClassName(dataStoreProps.getProperty(DRIVER_CLASSNAME));
		ds.setUsername(decrypter.decrypt(LOGIN_ID, dataStoreProps.getProperty(LOGIN_ID)));
		ds.setPassword(decrypter.decrypt(PASSWORD, dataStoreProps.getProperty(PASSWORD)));
		ds.setUrl(dataStoreProps.getProperty(URL));

		// the settings below are optional -- dbcp can work with
		// defaults
		ds.setMinIdle(5);
		ds.setMaxIdle(20);
		ds.setMaxOpenPreparedStatements(180);
	}

	@Override
	public void close()
	{
		try
		{
			Exception e = new Exception("Dummy Exception purely to identify the StackTrace of close."); 
			Logger.getInstance().warn("RDBMSDataStore for Service: " + serviceName + " close called. Location: " + e.getMessage(), e);
			ds.close();
		}
		catch (SQLException e)
		{
			Logger.getInstance().warn("SQLException during close of RDBMSDataStore for Service: " + serviceName + ". Reason: " + e.getMessage(), e);
		}
	}
	
	public Connection getConnection() throws SQLException
	{
		return this.ds.getConnection();
	}

	public static String createCallString(String storedProcName, int paramCount)
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
	
	public static String createFunctionString(String storedProcName, int paramCount)
	{
		StringBuffer sb = new StringBuffer(FUNC_BEGIN);
		sb.append(storedProcName).append("(");

		for (int i = 0; i < paramCount; ++i)
		{
			if (i != 0)
			{
				sb.append(",");
			}
			sb.append(FUNC_PARAM);
		}

		sb.append(FUNC_END);
		return sb.toString();
	}
}
