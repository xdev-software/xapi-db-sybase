package xdev.db.sybase.jdbc;

import java.sql.SQLException;

import com.xdev.jadoth.sqlengine.dbms.SQLExceptionParser;
import com.xdev.jadoth.sqlengine.exceptions.SQLEngineException;



public class SybaseExceptionParser implements SQLExceptionParser
{
	
	/**
	 * @see SQLExceptionParser#parseSQLException(SQLException)
	 */
	@Override
	public SQLEngineException parseSQLException(SQLException e)
	{
		return new SQLEngineException(e);
	}
	
}
