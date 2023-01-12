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
