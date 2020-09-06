package com.TBD.core.dao.rdbms;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is called only once after the ResultSet is obtained
 * from the execution of the StoredProc. If the implementation
 * for whatever reason needs to access the complete ResultSet
 * in creating it's final object this would be the interface.
 *
 */
public interface TotalResultSetProcessor
{
	public void process(ResultSet rs) throws SQLException;
}
