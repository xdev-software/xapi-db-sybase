package xdev.db.sybase.jdbc;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xdev.db.ColumnMetaData;
import xdev.db.DBException;
import xdev.db.DataType;
import xdev.db.Index;
import xdev.db.Index.IndexType;
import xdev.db.Result;
import xdev.db.StoredProcedure;
import xdev.db.StoredProcedure.Param;
import xdev.db.StoredProcedure.ParamType;
import xdev.db.StoredProcedure.ReturnTypeFlavor;
import xdev.db.jdbc.JDBCConnection;
import xdev.db.jdbc.JDBCMetaData;
import xdev.db.sql.Functions;
import xdev.db.sql.SELECT;
import xdev.db.sql.Table;
import xdev.util.ProgressMonitor;

import com.xdev.jadoth.sqlengine.interfaces.ConnectionProvider;


public class SybaseJDBCMetaData extends JDBCMetaData
{
	private static final long	serialVersionUID	= -5979795420664514298L;
	
	
	public SybaseJDBCMetaData(SybaseJDBCDataSource dataSource) throws DBException
	{
		super(dataSource);
	}
	
	
	@Override
	public TableInfo[] getTableInfos(ProgressMonitor monitor, EnumSet<TableType> types)
			throws DBException
	{
		monitor.beginTask("",ProgressMonitor.UNKNOWN);
		
		List<TableInfo> list = new ArrayList<>();
		
		try
		{
			
			try(JDBCConnection jdbcConnection = (JDBCConnection)dataSource.openConnection())
			{
				Connection connection = jdbcConnection.getConnection();
				DatabaseMetaData meta = connection.getMetaData();
				String catalog = getCatalog(dataSource);
				String schema = getSchema(dataSource);
				
				String[] castTypes = castEnumSetToStringArray(types);
				ResultSet rs = meta.getTables(catalog, schema, null, castTypes);
				while(rs.next() && !monitor.isCanceled())
				{
					String tableTypeName = rs.getString("TABLE_TYPE");
					TableType type = declareTableType(tableTypeName);
					
					if(types.contains(type))
					{
						String name = rs.getString("TABLE_NAME");
						list.add(new TableInfo(type, rs.getString("TABLE_SCHEM"), name));
					}
				}
				rs.close();
			}
		}
		catch(SQLException e)
		{
			throw new DBException(dataSource,e);
		}
		
		monitor.done();
		
		TableInfo[] tables = list.toArray(new TableInfo[list.size()]);
		Arrays.sort(tables);
		return tables;
	}
	
	/**
	 * Sets the TableType to the parsed TableTypeName
	 * @return {@link xdev.db.DBMetaData.TableType#TABLE} ||
	 * 		   {@link xdev.db.DBMetaData.TableType#VIEW} ||
	 * 		   {@link xdev.db.DBMetaData.TableType#OTHER}
	 */
	private static TableType declareTableType(String tableTypeName)
	{
		TableType type;
		if(tableTypeName != null
			&& tableTypeName.equalsIgnoreCase(TableType.TABLE.name()))
		{
			type = TableType.TABLE;
		}
		else if(tableTypeName != null
			&& tableTypeName.equalsIgnoreCase(TableType.VIEW.name()))
		{
			type = TableType.VIEW;
		}
		else
		{
			type = TableType.OTHER;
		}
		return type;
	}
	
	@Override
	protected TableMetaData getTableMetaData(JDBCConnection jdbcConnection, DatabaseMetaData meta,
			int flags, TableInfo table) throws DBException, SQLException
	{
		String catalog = getCatalog(dataSource);
		String schema = getSchema(dataSource);
		
		String tableName = table.getName();
		Table tableIdentity = new Table(tableName,"META_DUMMY");
		
		Map<String, Object> defaultValues = new HashMap<>();
		Map<String, Integer> columnSizes = new HashMap<>();
		ResultSet rs = meta.getColumns(catalog,schema,tableName,null);
		while(rs.next())
		{
			String columnName = rs.getString("COLUMN_NAME");
			defaultValues.put(columnName,rs.getObject("COLUMN_DEF"));
			columnSizes.put(columnName,rs.getInt("COLUMN_SIZE"));
		}
		rs.close();
		
		SELECT select = new SELECT().FROM(tableIdentity).WHERE("1 = 0");
		Result result = jdbcConnection.query(select);
		int cc = result.getColumnCount();
		ColumnMetaData[] columns = new ColumnMetaData[cc];
		
		for(int i = 0; i < cc; i++)
		{
			fillColumnsMetaData(tableName, defaultValues, columnSizes, result, columns, i);
		}
		result.close();
		
		Map<IndexInfo, Set<String>> indexMap = new HashMap<>();
		int count = UNKNOWN_ROW_COUNT;
		
		if(table.getType() == TableType.TABLE)
		{
			Set<String> primaryKeyColumns = new HashSet<>();
			rs = meta.getPrimaryKeys(catalog,schema,tableName);
			while(rs.next())
			{
				primaryKeyColumns.add(rs.getString("COLUMN_NAME"));
			}
			rs.close();
			
			if((flags & INDICES) != 0)
			{
				if(!primaryKeyColumns.isEmpty())
				{
					indexMap.put(new IndexInfo("PRIMARY_KEY",IndexType.PRIMARY_KEY),
							primaryKeyColumns);
				}
				
				rs = meta.getIndexInfo(catalog,schema,tableName,false,true);
				while(rs.next())
				{
					putColumnNamesInIndexMap(rs, indexMap, primaryKeyColumns);
				}
				rs.close();
			}
			
			if((flags & ROW_COUNT) != 0)
			{
				try
				{
					result = jdbcConnection.query(new SELECT().columns(Functions.COUNT()).FROM(
							tableIdentity));
					if(result.next())
					{
						count = result.getInt(0);
					}
					result.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		Index[] indices = new Index[indexMap.size()];
		int i = 0;
		for(IndexInfo indexInfo : indexMap.keySet())
		{
			fillIndicies(indexMap, indices, i, indexInfo);
			i++;
		}
		
		return new TableMetaData(table,columns,indices,count);
	}
	
	private static void fillIndicies(Map<IndexInfo, Set<String>> indexMap, Index[] indices, int i, IndexInfo indexInfo)
	{
		Set<String> columnList = indexMap.get(indexInfo);
		String[] indexColumns = columnList.toArray(new String[columnList.size()]);
		indices[i] = new Index(indexInfo.name, indexInfo.type,indexColumns);
	}
	
	private void putColumnNamesInIndexMap(ResultSet rs, Map<IndexInfo, Set<String>> indexMap, Set<String> primaryKeyColumns)
		throws SQLException
	{
		String indexName = rs.getString("INDEX_NAME");
		String columnName = rs.getString("COLUMN_NAME");
		if(indexName != null && columnName != null
				&& !primaryKeyColumns.contains(columnName))
		{
			boolean unique = !rs.getBoolean("NON_UNIQUE");
			IndexInfo info = new IndexInfo(indexName,unique ? IndexType.UNIQUE : IndexType.NORMAL);
			
			Set<String> columnNames = indexMap.get(info);
			if(columnNames == null)
			{
				columnNames = new HashSet<>();
				indexMap.put(info,columnNames);
			}
			columnNames.add(columnName);
		}
	}
	
	private void fillColumnsMetaData(
		String tableName,
		Map<String, Object> defaultValues,
		Map<String, Integer> columnSizes,
		Result result,
		ColumnMetaData[] columns,
		int i)
	{
		ColumnMetaData column = result.getMetadata(i);
		String name = column.getName();
		
		Object defaultValue = column.getDefaultValue();
		if(defaultValue == null && defaultValues.containsKey(name))
		{
			defaultValue = defaultValues.get(name);
		}
		defaultValue = checkDefaultValue(defaultValue,column);
		
		int length = column.getLength();
		if(length == 0 && columnSizes.containsKey(name))
		{
			DataType type = column.getType();
			if(type.isBlob() || type.isString())
			{
				length = columnSizes.get(name);
			}
		}
		
		columns[i] = new ColumnMetaData(tableName,name,column.getCaption(),column.getType(),
				length,column.getScale(),defaultValue,column.isNullable(),
				column.isAutoIncrement());
	}
	
	@Override
	public StoredProcedure[] getStoredProcedures(ProgressMonitor monitor) throws DBException
	{
		monitor.beginTask("",ProgressMonitor.UNKNOWN); //$NON-NLS-1$
		
		List<StoredProcedure> list = new ArrayList<>();
		
		try
		{
			ConnectionProvider<?> connectionProvider = dataSource.getConnectionProvider();
			
			try(Connection connection = connectionProvider.getConnection())
			{
				DatabaseMetaData meta = connection.getMetaData();
				
				try(Statement statement = meta.getConnection().createStatement())
				{
					ResultSet rs = statement
						.executeQuery(
							"select o.id, o.name AS PROCEDURE_NAME, c.name AS COLUMN_NAME, c.status2 AS COLUMN_TYPE, t.name, t.type AS DATA_TYPE, t.usertype from sysobjects o left join syscolumns c on c.id = o.id left join systypes t on t.usertype = c.usertype where o.type = 'SF' OR o.type = 'P' OR o.type = 'XP'"); //$NON-NLS-1$
					
					String oldName = ""; //$NON-NLS-1$
					List<Param> params = new ArrayList<>();
					int spIndex = 0;
					ReturnTypeFlavor returnTypeFlavor = null;
					DataType returnType = null;
					
					while(rs.next())
					{
						String name = rs.getString("PROCEDURE_NAME"); //$NON-NLS-1$
						
						if(!oldName.equals(name))
						{
							returnTypeFlavor = ReturnTypeFlavor.UNKNOWN;
							returnType = null;
						}
						
						int columnType = rs.getInt("COLUMN_TYPE"); //$NON-NLS-1$
						DataType dataType =
							sybToJdbc(rs.getInt("DATA_TYPE"), rs.getInt("usertype")); //$NON-NLS-1$ //$NON-NLS-2$
						String columnName = rs.getString("COLUMN_NAME"); //$NON-NLS-1$
						ParamType paramType = null;
						
						if(columnName != null && columnName.equals("Return Type")) //$NON-NLS-1$
						{
							returnTypeFlavor = ReturnTypeFlavor.TYPE;
							returnType = dataType;
						}
						else
						{
							if(columnType==1)
							{
								paramType = ParamType.IN;
							}
							else if(columnType==2)
							{
								paramType = ParamType.OUT;
							}
						}
						
						if(oldName.equals(name))
						{
							
							if(columnName != null && !columnName.equals("Return Type")) //$NON-NLS-1$
							{
								params.add(new Param(paramType, columnName, dataType));
							}
							StoredProcedure storedProcedure = new StoredProcedure(
								returnTypeFlavor,
								returnType,
								name,
								"",
								params.toArray(new Param[params.size()])
							); //$NON-NLS-1$
							
							list.set(spIndex - 1, storedProcedure);
						}
						else
						{
							
							params = new ArrayList<>();
							if(columnName != null && !columnName.equals("Return Type")) //$NON-NLS-1$
							{
								params.add(new Param(paramType, columnName, dataType));
							}
							list.add(new StoredProcedure(
								returnTypeFlavor,
								returnType,
								name,
								"",
								params.toArray(new Param[params.size()]))//$NON-NLS-1$
							);
							spIndex++;
							oldName = name;
						}
					}
				}
			}
		}
		catch(SQLException e)
		{
			throw new DBException(dataSource,e);
		}
		
		monitor.done();
		
		return list.toArray(new StoredProcedure[list.size()]);
	}
	
	
	private DataType sybToJdbc(int dataType, int userType)
	{
		switch(dataType)
		{
			case 48:
				return DataType.TINYINT;
				
			case 52:
				return DataType.SMALLINT;
				
			case 56:
				return DataType.INTEGER;
				
			case 191:
				return DataType.BIGINT;
				
			case 62:
				return DataType.FLOAT;
				
			case 59:
				return DataType.REAL;
				
			case 63:
				return DataType.NUMERIC;
				
			case 55:
				return DataType.DECIMAL;
				
			case 50:
				return DataType.BOOLEAN;
				
			case 47:
				return DataType.CHAR;
				
			case 35:
				return DataType.CLOB;
				
			case 45:
				return DataType.BINARY;
				
			case 37:
				if(userType == 80)
				{
					return DataType.TIMESTAMP;
				}
				return DataType.VARBINARY;
			
			case 49:
				return DataType.DATE;
				
			case 51:
				return DataType.TIME;
			
			default:
				return DataType.VARCHAR;
		}
	}
	
	
	@Override
	protected void createTable(JDBCConnection jdbcConnection, TableMetaData table)
			throws DBException, SQLException
	{
	}
	
	
	@Override
	protected void addColumn(JDBCConnection jdbcConnection, TableMetaData table,
			ColumnMetaData column, ColumnMetaData columnBefore, ColumnMetaData columnAfter)
			throws DBException, SQLException
	{
	}
	
	
	@Override
	protected void alterColumn(JDBCConnection jdbcConnection, TableMetaData table,
			ColumnMetaData column, ColumnMetaData existing) throws DBException, SQLException
	{
	}
	
	
	@Override
	public boolean equalsType(ColumnMetaData clientColumn, ColumnMetaData dbColumn)
	{
		return false;
	}
	
	
	@Override
	protected void dropColumn(JDBCConnection jdbcConnection, TableMetaData table,
			ColumnMetaData column) throws DBException, SQLException
	{
	}
	
	
	@Override
	protected void createIndex(JDBCConnection jdbcConnection, TableMetaData table, Index index)
			throws DBException, SQLException
	{
	}
	
	
	@Override
	protected void dropIndex(JDBCConnection jdbcConnection, TableMetaData table, Index index)
			throws DBException, SQLException
	{
	}
	
	
	@Override
	protected void appendEscapedName(String name, StringBuilder sb)
	{
	}
}
