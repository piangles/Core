package com.TBD.core.dao.rdbms;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.TBD.core.dao.DAOException;
import com.TBD.core.resources.RDBMSDataStore;

/**
 * All Data Access Object Implementations will extend this class.
 * 
 * The pattern across all layers and services is : We will be calling StoredProcs 
 * and there will not be any inline or generated SQL code. We are to leverage all
 * the functionality of the database that we are paying for instead of using the
 * generic features only.  
 *
 */
public class AbstractDAO
{
	private RDBMSDataStore dataStore = null;

	public void init(RDBMSDataStore dataStore)
	{
		this.dataStore = dataStore;
	}

	/**
	 * Call this method with the name of a StoredProc which returns a list of Strings.
	 * Ex: List of Currencies / List of Zipcodes
	 * 
	 * @param storedProcName
	 * @return
	 * @throws DAOException
	 */
	protected final List<String> executeSPQueryReturnAsList(String storedProcName) throws DAOException
	{
		List<String> values = new ArrayList<String>();
		
		executeSPQuery(storedProcName, new IndividualResultSetProcessor()
		{
			@Override
			public void process(ResultSet rs) throws SQLException
			{
				values.add(rs.getString(1));
			}
		}
		);
		
		return values;
	}

	/**
	 * Call this method with the name of StoredProc which returns a Map of Strings.
	 * Ex: ISOCountryCode-ISOCountryName / ConfigName-ConfigValue
	 * 
	 * @param storedProcName
	 * @return
	 * @throws DAOException
	 */
	protected final Map<String, String> executeSPQueryReturnAsMap(String storedProcName) throws DAOException
	{
		Map<String, String> valuesAsMap = new HashMap<String, String>();

		executeSPQuery(storedProcName, new IndividualResultSetProcessor()
		{
			@Override
			public void process(ResultSet rs) throws SQLException
			{
				valuesAsMap.put(rs.getString(1), rs.getString(2));
			}
		}
		);
		
		return valuesAsMap;
	}

	/**
	 * Call this method with the name of StoredProc and pass an implementation
	 * of IndividualResultSetProcessor. The expectation is the Impl will create 
	 * a List / Map as need be and convert RecordSet to Java objects of the 
	 * deisgned class. 
	 * 
	 * @param storedProcName
	 * @param irp
	 * @throws DAOException
	 */
	protected final void executeSPQuery(String storedProcName, IndividualResultSetProcessor irp) throws DAOException
	{
		executeSPQuery(storedProcName, 0, null, irp);
	}
	
	/**
	 * Call this method with the name of StoredProc which takes input parameters and creates
	 * it's result via IndividualResultSetProcessor. The purpose of the other 2 parameters are
	 * int paramCount : The number of ? to be put in the String for the call statement.
	 * StatementPreparer sp : The StatementPreparer Implementation is called to set the parameters
	 * with the proper type.
	 * 
	 * @param storedProcName
	 * @param paramCount
	 * @param sp
	 * @param irp
	 * @throws DAOException
	 */
	protected final void executeSPQuery(String storedProcName, int paramCount, StatementPreparer sp, IndividualResultSetProcessor irp) throws DAOException
	{
		Connection dbConnection = null;
		CallableStatement call = null;
		try
		{
			dbConnection = dataStore.getConnection();
			call = dbConnection.prepareCall(RDBMSDataStore.createCALLString(storedProcName, paramCount));
			if (sp != null)
			{
				sp.prepare(call);
			}

			ResultSet resultSet = call.executeQuery();
			while (resultSet.next())
			{
				irp.process(resultSet);
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
	}

	/**
	 * Call this method with the name of StoredProc which takes input parameters and creates
	 * it's result via TotalResultSetProcessor. The purpose of the other 2 parameters are
	 * int paramCount : The number of ? to be put in the String for the call statement.
	 * StatementPreparer sp : The StatementPreparer Implementation is called to set the parameters
	 * with the proper type.
	 * 
	 * TotalResultSetProcessor : Will only be called once after StoredProc is executed passing the
	 * complete ResultSet.
	 * 
	 * @param storedProcName
	 * @param paramCount
	 * @param sp
	 * @param trp
	 * @throws DAOException
	 */
	protected final void executeSPQuery(String storedProcName, int paramCount, StatementPreparer sp, TotalResultSetProcessor trp) throws DAOException
	{
		Connection dbConnection = null;
		CallableStatement call = null;
		try
		{
			dbConnection = dataStore.getConnection();
			call = dbConnection.prepareCall(RDBMSDataStore.createCALLString(storedProcName, paramCount));
			if (sp != null)
			{
				sp.prepare(call);
			}

			ResultSet resultSet = call.executeQuery();
			trp.process(resultSet);
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
	
	/**
	 * This is the method one would use if they want to call a StoredProc that either
	 * 1. Updates a record or records.
	 * 2. Deletes a record or records.
	 * 
	 *  Hence the reason no Result processing is done. Also pass in the Transaction scope. 
	 *  Default scope is within this method. And outside scope if given in. 
	 * 
	 * @param storedProcName
	 * @param paramCount
	 * @param sp
	 * @throws DAOException
	 */
	protected final void executeSP(String storedProcName, int paramCount, StatementPreparer sp) throws DAOException
	{
		Connection dbConnection = null;
		CallableStatement call = null;
		try
		{
			dbConnection = dataStore.getConnection();
			call = dbConnection.prepareCall(RDBMSDataStore.createCALLString(storedProcName, paramCount));
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
}
