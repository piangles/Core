package org.piangles.core.dao.rdbms;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * This is called only Once to set the parameters on the CallableStatement
 * before the CallableStatement is executed.
 */
public interface StatementPreparer
{
	public void prepare(CallableStatement call) throws SQLException;
}
