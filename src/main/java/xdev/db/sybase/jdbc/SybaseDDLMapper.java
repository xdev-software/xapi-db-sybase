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

import java.util.Hashtable;

import com.xdev.jadoth.sqlengine.SQL;
import com.xdev.jadoth.sqlengine.SQL.DATATYPE;
import com.xdev.jadoth.sqlengine.SQL.INDEXTYPE;
import com.xdev.jadoth.sqlengine.dbms.standard.StandardDDLMapper;
import com.xdev.jadoth.sqlengine.internal.tables.SqlTableIdentity;


public class SybaseDDLMapper extends StandardDDLMapper<SybaseDbms>
{
	// /////////////////////////////////////////////////////////////////////////
	// constants //
	// ///////////////////
	
	/** The Constant DATATYPE_BIT. */
	public static final String							DATATYPE_BIT		= "BIT";
	
	/** The Constant DATATYPE_DATETIME. */
	public static final String							DATATYPE_DATETIME	= "DATETIME";
	
	/** The Constant DATATYPE_TEXT. */
	public static final String							DATATYPE_TEXT		= "TEXT";
	
	/** The Constant DATATYPE_IMAGE. */
	public static final String							DATATYPE_IMAGE		= "IMAGE";
	
	/** The Constant dataTypeStrings. */
	private static final Hashtable<String, DATATYPE>	dataTypeStrings		= createDataTypeStrings();
	
	
	/**
	 * Creates the data type strings.
	 * 
	 * @return the hashtable
	 */
	private static Hashtable<String, DATATYPE> createDataTypeStrings()
	{
		final Hashtable<String, DATATYPE> c = new Hashtable<>(10);
		
		c.put(DATATYPE_BIT,SQL.DATATYPE.BOOLEAN);
		
		c.put(DATATYPE_DATETIME,SQL.DATATYPE.TIMESTAMP);
		
		c.put(DATATYPE_TEXT,SQL.DATATYPE.LONGVARCHAR);
		c.put(DATATYPE_IMAGE,SQL.DATATYPE.LONGVARBINARY);
		
		c.put(SQL.DATATYPE.FLOAT.name(),SQL.DATATYPE.DOUBLE);
		
		// replicate the whole stuff for lower case, just in case
		for(final String s : c.keySet().toArray(new String[c.size()]))
		{
			c.put(s.toLowerCase(),c.get(s));
		}
		
		return c;
	}
	
	
	// /////////////////////////////////////////////////////////////////////////
	// constructors //
	// ///////////////////
	
	/**
	 * @param dbmsAdaptor
	 *            the dbms adaptor
	 */
	protected SybaseDDLMapper(final SybaseDbms dbmsAdaptor)
	{
		super(dbmsAdaptor);
	}
	
	
	// /////////////////////////////////////////////////////////////////////////
	// override methods //
	// ///////////////////
	
	/**
	 * Map data type.
	 *
	 * @see com.xdev.jadoth.sqlengine.dbms.DbmsDDLMapper#mapDataType(String)
	 */
	@Override
	public DATATYPE mapDataType(final String dataTypeString)
	{
		final String upperCaseDataType = dataTypeString.toUpperCase();
		
		final DATATYPE dbmsType = dataTypeStrings.get(upperCaseDataType);
		if(dbmsType != null)
		{
			return dbmsType;
		}
		
		return super.mapDataType(upperCaseDataType);
	}
	
	
	/**
	 * Map index type.
	 * @see com.xdev.jadoth.sqlengine.dbms.DbmsDDLMapper#mapIndexType(String)
	 */
	@Override
	public INDEXTYPE mapIndexType(final String indexTypeString)
	{
		if(indexTypeString == null)
		{
			return SQL.INDEXTYPE.NORMAL;
		}
		
		if(indexTypeString.contains("unique"))
		{
			return SQL.INDEXTYPE.UNIQUE;
		}
		
		return SQL.INDEXTYPE.NORMAL;
	}
	
	
	/**
	 * Lookup ddbms data type mapping.
	 * @see com.xdev.jadoth.sqlengine.dbms.DbmsDDLMapper#lookupDdbmsDataTypeMapping(DATATYPE, SqlTableIdentity)
	 */
	@Override
	public String lookupDdbmsDataTypeMapping(final DATATYPE type, final SqlTableIdentity table)
	{
		switch(type)
		{
			case BOOLEAN:
				return DATATYPE_BIT;
			case TIMESTAMP:
				return DATATYPE_DATETIME;
			case LONGVARCHAR:
				return DATATYPE_TEXT;
			case DOUBLE:
				return SQL.DATATYPE.FLOAT.name();
			case FLOAT:
				return SQL.DATATYPE.REAL.name();
			default:
				return super.lookupDdbmsDataTypeMapping(type,table);
		}
	}
	
}
