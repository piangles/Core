package com.TBD.core.dao.rdbms;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetProcessor
{
	public void process(ResultSet rs) throws SQLException;
}
