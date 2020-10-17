package org.piangles.core.dao.rdbms;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetProcessor<T>
{
	public T process(ResultSet rs) throws SQLException;
}
