/*
 * xapi-db-sybase - XAPI SqlEngine Database Adapter for Sybase
 * Copyright © 2023 XDEV Software (https://xdev.software)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package xdev.db.sybase.jdbc;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import xdev.db.DBException;
import xdev.db.jdbc.JDBCConnection;
import xdev.db.locking.PessimisticLockTableConstants;


public class SybaseJDBCConnection extends JDBCConnection<SybaseJDBCDataSource, SybaseDbms>
{
	public SybaseJDBCConnection(SybaseJDBCDataSource dataSource)
	{
		super(dataSource);
	}
	
	
	@Override
	protected void setPreparedStatementParameter(PreparedStatement statement, Object parameter,
			int jdbcIndex) throws SQLException, DBException
	{
		// Issue 11855 Workaround - if the parameter is null
		// the setObject()-Method throws an Exception
		if(parameter == null)
		{
			statement.setNull(jdbcIndex,Types.VARCHAR);
		}
		else if(parameter instanceof Blob)
		{
			Blob blob = (Blob)parameter;
			statement.setBinaryStream(jdbcIndex,blob.getBinaryStream(),(int)blob.length());
		}
		else if(parameter instanceof Clob)
		{
			Clob clob = (Clob)parameter;
			statement.setString(jdbcIndex,clob.getSubString(1,(int)clob.length()));
		}
		else
		{
			super.setPreparedStatementParameter(statement,parameter,jdbcIndex);
		}
	}
	
	
	@Override
	public void createTable(String tableName, String primaryKey, Map<String, String> columnMap,
			boolean isAutoIncrement, Map<String, String> foreignKeys) throws Exception
	{
		
		Connection connection = super.getConnection();
		
		if(!checkIfTableExists(connection.createStatement(),tableName))
		{
			
			if(!columnMap.containsKey(primaryKey))
			{
				columnMap.put(primaryKey,"INTEGER"); //$NON-NLS-1$
			}
			
			StringBuffer createStatement = initCreateStatement(tableName, primaryKey, columnMap);
			
			
			if(log.isDebugEnabled())
			{
				log.debug("SQL Statement to create a table: " + createStatement.toString()); //$NON-NLS-1$
			}
			
			try(Statement statement = connection.createStatement())
			{
				statement.execute(createStatement.toString());
			}
			catch(Exception e)
			{
				throw e;
			}
			finally
			{
				connection.close();
			}
		}
	}
	
	private static StringBuffer initCreateStatement(String tableName, String primaryKey, Map<String, String> columnMap)
	{
		StringBuffer createStatement = new StringBuffer("CREATE TABLE " + tableName //$NON-NLS-1$
				+ "(" + primaryKey + " " + columnMap.get(primaryKey) + " IDENTITY NOT NULL,"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		for(String keySet : columnMap.keySet())
		{
			if(!keySet.equals(primaryKey))
			{
				if(columnMap.get(keySet).contains(PessimisticLockTableConstants.TIMESTAMP_TEXT))
				{
					String replace = columnMap.get(keySet).replace(
							PessimisticLockTableConstants.TIMESTAMP_TEXT,
							PessimisticLockTableConstants.DATETIME_TEXT);
					createStatement.append(keySet)
						.append(" ")
						.append(replace)
						.append(","); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else
				{
					createStatement.append(keySet)
						.append(" ")
						.append(columnMap.get(keySet))
						.append(","); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		
		createStatement.append(" PRIMARY KEY (")
			.append(primaryKey)
			.append("))"); //$NON-NLS-1$ //$NON-NLS-2$
		
		return createStatement;
	}
	
	private boolean checkIfTableExists(Statement statement, String tableName) throws Exception
	{
		
		String sql = "SELECT * FROM sysobjects WHERE name = '" + tableName + "'"; //$NON-NLS-1$ //$NON-NLS-2$
		
		if(log.isDebugEnabled())
		{
			log.debug(sql);
		}
		
		ResultSet resultSet = null;
		try
		{
			statement.execute(sql);
			resultSet = statement.getResultSet();
		}
		catch(Exception e)
		{
			if(resultSet != null)
			{
				resultSet.close();
			}
			statement.close();
			throw e;
		}
		
		if(resultSet != null)
		{
			if(resultSet.next())
			{
				resultSet.close();
				statement.close();
				return true;
			}
			resultSet.close();
			
		}
		statement.close();
		return false;
	}
	
	
	@Override
	public Date getServerTime() throws DBException, ParseException
	{
		String selectTime = "SELECT GETDATE()"; //$NON-NLS-1$
		return super.getServerTime(selectTime);
	}
	
}
