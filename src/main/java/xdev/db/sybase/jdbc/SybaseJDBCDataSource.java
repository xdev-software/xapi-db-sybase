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
