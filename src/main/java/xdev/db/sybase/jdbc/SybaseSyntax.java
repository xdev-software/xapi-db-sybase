package xdev.db.sybase.jdbc;


import com.xdev.jadoth.sqlengine.dbms.DbmsSyntax;


public class SybaseSyntax extends DbmsSyntax.Implementation<SybaseDbms>
{
	protected SybaseSyntax()
	{
		super(wordSet(),wordSet());
	}
	
}
