package xdev.db.sybase.jdbc;


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
