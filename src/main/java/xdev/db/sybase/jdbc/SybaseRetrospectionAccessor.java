package xdev.db.sybase.jdbc;

/*-
 * #%L
 * SqlEngine Database Adapter Sybase
 * %%
 * Copyright (C) 2003 - 2023 XDEV Software
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */


import com.xdev.jadoth.sqlengine.dbms.standard.StandardRetrospectionAccessor;
import com.xdev.jadoth.sqlengine.exceptions.SQLEngineException;
import com.xdev.jadoth.sqlengine.internal.tables.SqlIndex;
import com.xdev.jadoth.sqlengine.internal.tables.SqlTableIdentity;


public class SybaseRetrospectionAccessor extends StandardRetrospectionAccessor<SybaseDbms>
{
	public static final String RETROSPECTION_NOT_IMPLEMENTED_YET = "Retrospection not implemented yet!";
	// /////////////////////////////////////////////////////////////////////////
	// constructors //
	// ///////////////////

	public SybaseRetrospectionAccessor(final SybaseDbms dbmsadaptor)
	{
		super(dbmsadaptor);
	}
	
	
	// /////////////////////////////////////////////////////////////////////////
	// override methods //
	// ///////////////////
	/**
	 * @see com.xdev.jadoth.sqlengine.dbms.DbmsRetrospectionAccessor#createSelect_INFORMATION_SCHEMA_COLUMNS(SqlTableIdentity)
	 */
	@Override
	public String createSelect_INFORMATION_SCHEMA_COLUMNS(final SqlTableIdentity table)
	{
		throw new RuntimeException(RETROSPECTION_NOT_IMPLEMENTED_YET);
	}
	
	
	/**
	 * @see com.xdev.jadoth.sqlengine.dbms.DbmsRetrospectionAccessor#createSelect_INFORMATION_SCHEMA_INDICES(SqlTableIdentity)
	 */
	@Override
	public String createSelect_INFORMATION_SCHEMA_INDICES(final SqlTableIdentity table)
	{
		throw new RuntimeException(RETROSPECTION_NOT_IMPLEMENTED_YET);
	}
	
	
	/**
	 * @throws SQLEngineException
	 * @see com.xdev.jadoth.sqlengine.dbms.DbmsRetrospectionAccessor#loadIndices(SqlTableIdentity)
	 */
	@Override
	public SqlIndex[] loadIndices(final SqlTableIdentity table) throws SQLEngineException
	{
		throw new RuntimeException(RETROSPECTION_NOT_IMPLEMENTED_YET);
	}
}
