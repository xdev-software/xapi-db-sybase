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

import java.sql.Connection;
import java.util.Properties;

import com.xdev.jadoth.sqlengine.dbms.DbmsConnectionInformation;
import xdev.db.ConnectionInformation;


public class SybaseConnectionInformation extends ConnectionInformation<SybaseDbms>
{
	// /////////////////////////////////////////////////////////////////////////
	// constructors //
	// ///////////////////
	
	public SybaseConnectionInformation(final String host, final int port, final String user,
			final String password, final String database, final String urlExtension,
			final SybaseDbms dbmsAdaptor)
	{
		super(host,port,user,password,database,urlExtension,dbmsAdaptor);
	}
	
	
	// /////////////////////////////////////////////////////////////////////////
	// getters //
	// ///////////////////
	
	/**
	 * Gets the database.
	 * 
	 * @return the database
	 */
	public String getDatabase()
	{
		return this.getCatalog();
	}
	
	
	// /////////////////////////////////////////////////////////////////////////
	// setters //
	// ///////////////////
	
	/**
	 * Sets the database.
	 * 
	 * @param database
	 *            the database to set
	 */
	public void setDatabase(final String database)
	{
		this.setCatalog(database);
	}
	
	
	// /////////////////////////////////////////////////////////////////////////
	// override methods //
	// ///////////////////
	
	/**
	 * @see DbmsConnectionInformation#createJdbcConnectionUrl()
	 */
	@Override
	public String createJdbcConnectionUrl()
	{
		String url = "jdbc:sybase:Tds:" + getHost() + ":" + getPort() + "/" + getDatabase();
		return appendUrlExtension(url);
	}
	
	
	@Override
	protected Properties createConnectionProperties()
	{
		Properties p = super.createConnectionProperties();
		p.put("USE_METADATA","false");
		return p;
	}
	
	
	/**
	 * @see DbmsConnectionInformation#getJdbcDriverClassName()
	 */
	@Override
	public String getJdbcDriverClassName()
	{
		return "com.sybase.jdbc3.jdbc.SybDriver";
	}
	
	
	@Override
	public boolean isConnectionValid(Connection connection)
	{
		try
		{
			return !connection.isClosed();
		}
		catch(Throwable ignored)
		{
		}
		
		return false;
	}
}
