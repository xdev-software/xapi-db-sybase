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

import xdev.db.DBException;
import xdev.db.jdbc.JDBCDataSource;


public class SybaseJDBCDataSource extends JDBCDataSource<SybaseJDBCDataSource, SybaseDbms>
{
	public SybaseJDBCDataSource()
	{
		super(new SybaseDbms());
	}
	
	
	@Override
	public Parameter[] getDefaultParameters()
	{
		return new Parameter[]{
			HOST.clone(),
			PORT.clone(5000),
			USERNAME.clone("SA"),
			PASSWORD.clone(),
			CATALOG.clone(),
			URL_EXTENSION.clone(),
			IS_SERVER_DATASOURCE.clone(),
			SERVER_URL.clone(),
			AUTH_KEY.clone()
		};
	}
	
	
	@Override
	protected SybaseConnectionInformation getConnectionInformation()
	{
		return new SybaseConnectionInformation(
			getHost(),
			getPort(),
			getUserName(),
			getPassword().getPlainText(),
			getCatalog(),
			getUrlExtension(),
			getDbmsAdaptor()
		);
	}
	
	
	@Override
	public SybaseJDBCConnection openConnectionImpl() throws DBException
	{
		return new SybaseJDBCConnection(this);
	}
	
	
	@Override
	public SybaseJDBCMetaData getMetaData() throws DBException
	{
		return new SybaseJDBCMetaData(this);
	}
	
	
	@Override
	public boolean canExport()
	{
		return false;
	}
}
