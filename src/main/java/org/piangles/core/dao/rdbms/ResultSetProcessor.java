package org.piangles.core.dao.rdbms;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetProcessor<T>
{
	public T process(ResultSet rs, CallableStatement call) throws SQLException;
}
