
package xdev.db.sybase.jdbc;

import com.xdev.jadoth.sqlengine.dbms.DbmsAdaptor;
import com.xdev.jadoth.sqlengine.dbms.SQLExceptionParser;
import com.xdev.jadoth.sqlengine.interfaces.ConnectionProvider;
import com.xdev.jadoth.sqlengine.internal.DatabaseGateway;
import com.xdev.jadoth.sqlengine.internal.tables.SqlTableIdentity;


public class SybaseDbms
		extends
		DbmsAdaptor.Implementation<SybaseDbms, SybaseDMLAssembler, SybaseDDLMapper, SybaseRetrospectionAccessor, SybaseSyntax>
{
	// /////////////////////////////////////////////////////////////////////////
	// constants //
	// ///////////////////
	
	/** The Constant MAX_VARCHAR_LENGTH. */
	protected static final int	MAX_VARCHAR_LENGTH		= 4000;
	
	protected static final char	IDENTIFIER_DELIMITER	= '"';
	
	
	// /////////////////////////////////////////////////////////////////////////
	// static methods //
	// /////////////////
	
	public static ConnectionProvider<SybaseDbms> singleConnection(final String host,
			final int port, final String user, final String password, final String database, final String properties)
	{
		return new ConnectionProvider.Body<>(new SybaseConnectionInformation(
			host,
			port,
			user,
			password,
			database,
			properties,
			new SybaseDbms()
		));
	}
	
	
	// /////////////////////////////////////////////////////////////////////////
	// constructors //
	// ///////////////
	
	public SybaseDbms()
	{
		this(new SybaseExceptionParser());
	}
	
	
	public SybaseDbms(final SQLExceptionParser sqlExceptionParser)
	{
		super(sqlExceptionParser,false);
		this.setRetrospectionAccessor(new SybaseRetrospectionAccessor(this));
		this.setDMLAssembler(new SybaseDMLAssembler(this));
		this.setDdlMapper(new SybaseDDLMapper(this));
		this.setSyntax(new SybaseSyntax());
	}
	
	
	/**
	 * @see DbmsAdaptor#createConnectionInformation(String, int, String, String, String, String)
	 */
	@Override
	public SybaseConnectionInformation createConnectionInformation(final String host,
			final int port, final String user, final String password, final String catalog, final String properties)
	{
		return new SybaseConnectionInformation(host,port,user,password,catalog,properties, this);
	}
	
	
	/**
	 * @see DbmsAdaptor#updateSelectivity(SqlTableIdentity)
	 */
	@Override
	public Object updateSelectivity(final SqlTableIdentity table)
	{
		return null;
	}
	
	
	/**
	 * @see DbmsAdaptor#assembleTransformBytes(byte[], StringBuilder)
	 */
	@Override
	public StringBuilder assembleTransformBytes(final byte[] bytes, final StringBuilder sb)
	{
		return sb;
	}
	
	
	/**
	 * @see DbmsAdaptor#initialize(DatabaseGateway)
	 */
	@Override
	public void initialize(final DatabaseGateway<SybaseDbms> dbc)
	{
	}
	
	/**
	 * @see DbmsAdaptor#rebuildAllIndices(String)
	 */
	@Override
	public Object rebuildAllIndices(final String fullQualifiedTableName)
	{
		return null;
	}
	
	
	/**
	 * @see DbmsAdaptor#getMaxVARCHARlength()
	 */
	@Override
	public int getMaxVARCHARlength()
	{
		return MAX_VARCHAR_LENGTH;
	}
	
	
	@Override
	public boolean supportsOFFSET_ROWS()
	{
		return false;
	}
	
	
	@Override
	public char getIdentifierDelimiter()
	{
		return IDENTIFIER_DELIMITER;
	}
}
