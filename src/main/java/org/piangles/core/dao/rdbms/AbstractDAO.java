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
 
 
 
package org.piangles.core.dao.rdbms;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.piangles.core.dao.DAOException;
import org.piangles.core.resources.RDBMSDataStore;

/**
 * All Data Access Object Implementations will extend this class.
 * 
 * The pattern across all layers and services is : We will be calling
 * StoredProcs and there will not be any inline or generated SQL code. We are to
 * leverage all the functionality of the database that we are paying for instead
 * of using the generic features only.
 *
 */
public abstract class AbstractDAO
{
	private RDBMSDataStore dataStore = null;

	public void init(RDBMSDataStore dataStore)
	{
		this.dataStore = dataStore;
	}

	/**
	 * Call this method with the name of StoredProc and pass an implementation
	 * of IndividualResultSetProcessor. The expectation is the Impl will create
	 * a List / Map as need be and convert RecordSet to Java objects of the
	 * deisgned class.
	 * 
	 * @param storedProcName Name of stored procedure to be called.
	 * @param rsp An instance of ResultSetProcessor lambda that will be called to process the resultset.
	 * @throws DAOException if a database access error occurs or this method is called on a closed connection.
	 * @return List of POJOs created by ResultSetProcessor.  
	 */
	
	protected final <T> List<T> executeSPQueryList(String storedProcName, ResultSetProcessor<T> rsp) throws DAOException
	{
		return executeSPQueryList(storedProcName, 0, null, rsp);
	}

	/**
	 * Call this method with the name of StoredProc which takes input parameters
	 * and creates it's result via IndividualResultSetProcessor. The purpose of
	 * the other 2 parameters are int paramCount : The number of ? to be put in
	 * the String for the call statement. StatementPreparer sp : The
	 * StatementPreparer Implementation is called to set the parameters with the
	 * proper type.
	 * 
	 * @param storedProcName Name of stored procedure to be called.
	 * @param paramCount Number of paramters that the stored procedure accepts.
	 * @param sp Instance of StatementPreparer that sets the params.
	 * @param rsp An instance of ResultSetProcessor lambda that will be called to process the resultset.
	 * @throws DAOException if a database access error occurs or this method is called on a closed connection.
	 */
	protected final <T> List<T> executeSPQueryList(String storedProcName, int paramCount, StatementPreparer sp, ResultSetProcessor<T> rsp) throws DAOException
	{
		return execute(storedProcName, true, paramCount, sp, rsp, true);
	}

	/**
	 * Call this method with the name of StoredProc which takes input parameters
	 * and creates it's result via TotalResultSetProcessor. The purpose of the
	 * other 2 parameters are int paramCount : The number of ? to be put in the
	 * String for the call statement. StatementPreparer sp : The
	 * StatementPreparer Implementation is called to set the parameters with the
	 * proper type.
	 * 
	 * ResultSetProcessor : Will only be called once after StoredProc is
	 * executed passing the complete ResultSet.
	 * 
	 * This is called only once after the ResultSet is obtained from the
	 * execution of the StoredProc. If the implementation for whatever reason
	 * needs to access the complete ResultSet in creating it's final object this
	 * would be the interface.
	 * 
	 * @param storedProcName Name of stored procedure to be called.
	 * @param paramCount Number of paramters that the stored procedure accepts.
	 * @param sp Instance of StatementPreparer that sets the params.
	 * @param rsp An instance of ResultSetProcessor lambda that will be called to process the resultset.
	 * @throws DAOException if a database access error occurs or this method is called on a closed connection.
	 */
	protected final <T> T executeSPQuery(String storedProcName, int paramCount, StatementPreparer sp, ResultSetProcessor<T> rsp) throws DAOException
	{
		T retValue = null;
		List<T> results = null;

		results = execute(storedProcName, true, paramCount, sp, rsp, false);

		if (results != null && results.size() >= 1)
		{
			retValue = results.get(0);
		}

		return retValue;
	}

	/**
	 * This is the method one would use if they want to call a StoredProc that
	 * either 1. Updates a record or records. 2. Deletes a record or records.
	 * 
	 * Hence the reason no Result processing is done. Also pass in the
	 * Transaction scope. Default scope is within this method. And outside scope
	 * if given in.
	 * 
	 * @param storedProcName Name of stored procedure to be called.
	 * @param paramCount Number of paramters that the stored procedure accepts.
	 * @param sp Instance of StatementPreparer that sets the params.
	 * @throws DAOException if a database access error occurs or this method is called on a closed connection.
	 * 
	 */
	protected final void executeSP(String storedProcName, int paramCount, StatementPreparer sp) throws DAOException
	{
		Connection dbConnection = null;
		CallableStatement call = null;
		try
		{
			dbConnection = dataStore.getConnection();
			call = dbConnection.prepareCall(RDBMSDataStore.createCallString(storedProcName, paramCount));
			sp.prepare(call);

			call.execute();
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			if (dbConnection != null)
			{
				try
				{
					dbConnection.close();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private final <T> List<T> execute(String storedFuncOrProcName, boolean isFunction, int paramCount, StatementPreparer sp, ResultSetProcessor<T> irp, boolean complete) throws DAOException
	{
		List<T> results = null;
		Connection dbConnection = null;
		CallableStatement call = null;
		try
		{
			dbConnection = dataStore.getConnection();
			if (isFunction)
			{
				call = dbConnection.prepareCall(RDBMSDataStore.createFunctionString(storedFuncOrProcName, paramCount));
			}
			else
			{
				call = dbConnection.prepareCall(RDBMSDataStore.createCallString(storedFuncOrProcName, paramCount));
			}
			if (sp != null)
			{
				sp.prepare(call);
			}

			boolean result = call.execute();
			if (result)
			{
				ResultSet resultSet = call.getResultSet();
				while (resultSet.next())
				{
					if (results == null)
					{
						results = new ArrayList<T>();
					}
					results.add(irp.process(resultSet, call));
					if (!complete)
					{
						break;
					}
				}
			}
			else
			{
				results = new ArrayList<T>();
				results.add(irp.process(null, call));
			}
		}
		catch (SQLException e)
		{
			throw new DAOException(e);
		}
		finally
		{
			if (dbConnection != null)
			{
				try
				{
					dbConnection.close();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
		return results;
	}
}
