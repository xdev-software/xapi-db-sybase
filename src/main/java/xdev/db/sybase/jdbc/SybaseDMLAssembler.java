package xdev.db.sybase.jdbc;


import static com.xdev.jadoth.sqlengine.SQL.LANG.TOP;
import static com.xdev.jadoth.sqlengine.SQL.LANG.UNION;
import static com.xdev.jadoth.sqlengine.SQL.LANG.UNION_ALL;
import static com.xdev.jadoth.sqlengine.SQL.Punctuation._;
import static com.xdev.jadoth.sqlengine.internal.QueryPart.ASEXPRESSION;
import static com.xdev.jadoth.sqlengine.internal.QueryPart.OMITALIAS;
import static com.xdev.jadoth.sqlengine.internal.QueryPart.QUALIFY_BY_TABLE;
import static com.xdev.jadoth.sqlengine.internal.QueryPart.UNQUALIFIED;
import static com.xdev.jadoth.sqlengine.internal.QueryPart.indent;
import static com.xdev.jadoth.sqlengine.internal.QueryPart.isSingleLine;


import com.xdev.jadoth.sqlengine.DELETE;
import com.xdev.jadoth.sqlengine.INSERT;
import com.xdev.jadoth.sqlengine.SELECT;
import com.xdev.jadoth.sqlengine.UPDATE;
import com.xdev.jadoth.sqlengine.dbms.standard.StandardDMLAssembler;
import com.xdev.jadoth.sqlengine.types.ConditionalTableQuery;
import com.xdev.jadoth.sqlengine.types.WritingTableQuery;


public class SybaseDMLAssembler extends StandardDMLAssembler<SybaseDbms>
{
	// /////////////////////////////////////////////////////////////////////////
	// constants //
	// ///////////////////
	/** The Constant _TOP_. */
	protected static final String	_TOP_	= _ + TOP + _;
	
	
	// /////////////////////////////////////////////////////////////////////////
	// constructors //
	// ///////////////////
	
	public SybaseDMLAssembler(final SybaseDbms dbms)
	{
		super(dbms);
	}
	
	
	// /////////////////////////////////////////////////////////////////////////
	// override methods //
	// ///////////////////
	/**
	 * @see StandardDMLAssembler#assembleSELECT(SELECT, StringBuilder, int, int, String, String)
	 */
	@Override
	protected StringBuilder assembleSELECT(final SELECT query, final StringBuilder sb,
			final int indentLevel, final int flags, final String clauseSeperator,
			final String newLine)
	{
		indent(sb,indentLevel,isSingleLine(flags)).append(query.keyword());
		
		this.assembleSelectDISTINCT(query,sb,indentLevel,flags);
		this.assembleSelectRowLimit(query,sb,flags,clauseSeperator,newLine,indentLevel);
		this.assembleSelectItems(query,sb,flags,indentLevel,newLine);
		this.assembleSelectSqlClauses(query,sb,indentLevel,flags | ASEXPRESSION,clauseSeperator,newLine);
		this.assembleAppendSELECTs(query,sb,indentLevel,flags,clauseSeperator,newLine);
		
		return sb;
	}
	
	
	/**
	 * @see StandardDMLAssembler#assembleAppendSELECTs(SELECT, StringBuilder, int, int, String, String)
	 */
	@Override
	protected StringBuilder assembleAppendSELECTs(final SELECT query, final StringBuilder sb,
			final int indentLevel, final int flags, final String clauseSeperator,
			final String newLine)
	{
		SELECT appendSelect = query.getUnionSelect();
		if(appendSelect != null)
		{
			this.assembleAppendSelect(
				appendSelect,
				sb,
				indentLevel,
				flags,
				clauseSeperator,
				newLine,
				UNION
			);
			return sb;
		}
		
		appendSelect = query.getUnionAllSelect();
		if(appendSelect != null)
		{
			this.assembleAppendSelect(
				appendSelect,
				sb,
				indentLevel,
				flags,
				clauseSeperator,
				newLine,
				UNION_ALL
			);
			return sb;
		}
		return sb;
	}
	
	
	/**
	 * @see StandardDMLAssembler#assembleSelectRowLimit(SELECT, StringBuilder, int, String, String, int)
	 */
	@Override
	protected StringBuilder assembleSelectRowLimit(final SELECT query, final StringBuilder sb,
			final int flags, final String clauseSeperator, final String newLine,
			final int indentLevel)
	{
		final Integer top = query.getFetchFirstRowCount();
		if(top != null)
		{
			sb.append(_TOP_).append(top);
		}
		return sb;
	}
	
	
	/**
	 * Writing queries flags.
	 */
	protected int writingQueriesFlags(final WritingTableQuery query, final int flags)
	{
		return flags
				| OMITALIAS
				| (query instanceof ConditionalTableQuery
						&& ((ConditionalTableQuery)query).getFromClause() != null ? QUALIFY_BY_TABLE
						: UNQUALIFIED);
	}
	
	
	/**
	 * @see StandardDMLAssembler#assembleDELETE(DELETE, StringBuilder, int, String, String, int)
	 */
	@Override
	protected StringBuilder assembleDELETE(final DELETE query, final StringBuilder sb,
			final int flags, final String clauseSeperator, final String newLine,
			final int indentLevel)
	{
		return super.assembleDELETE(query,sb,this.writingQueriesFlags(query,flags),clauseSeperator,
				newLine,indentLevel);
	}
	
	
	/**
	 * @see StandardDMLAssembler#assembleINSERT(INSERT, StringBuilder, int, String, String, int)
	 */
	@Override
	protected StringBuilder assembleINSERT(final INSERT query, final StringBuilder sb,
			final int flags, final String clauseSeperator, final String newLine,
			final int indentLevel)
	{
		return super.assembleINSERT(
			query,
			sb,
			this.writingQueriesFlags(query,flags),
			clauseSeperator,
			newLine,
			indentLevel
		);
	}
	
	
	/**
	 * @see StandardDMLAssembler#assembleUPDATE(UPDATE, StringBuilder, int, int, String, String)
	 */
	@Override
	protected StringBuilder assembleUPDATE(final UPDATE query, final StringBuilder sb,
			final int indentLevel, final int flags, final String clauseSeperator,
			final String newLine)
	{
		return super.assembleUPDATE(
			query,
			sb,
			indentLevel,
			this.writingQueriesFlags(query,flags),
			clauseSeperator,
			newLine);
	}
	
}
