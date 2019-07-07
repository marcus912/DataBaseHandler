package marcus.utils.database;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import marcus.utils.BA_TOOLS;

/**
 * 實作 DataBaseActionInterFace
 * doInsert/doUpdate/doDelete 宣告為 abstract，在子類別實作
 * @author marcus.lin
 *
 */
public abstract class DataBaseHandler implements DataBaseActionInterFace {
//	private static DataBaseHandler handler;

	Logger logger = Logger.getLogger(DataBaseHandler.class);
	public static final String errorKey = DataBaseHandler.class.getName();

	BA_TOOLS tools = BA_TOOLS.getInstance();
	public static final String dot = ".";
	public static final String comma = ",";
	public static final String questionMark = "?";
	public static final String openParenthesis = "(";
	public static final String closeParenthesis = ")";
	public static final String equalTo = "=";
	public static final String slash = "/";
	public static final String sysDate = "SYSDATE";
	//日期格式物件宣告
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat dateSlashFormat = new SimpleDateFormat("yyyy/MM/dd");
	public static final SimpleDateFormat dateTimeSlashFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/**
	 * 特殊處理的欄位宣告
	 *
	 */
	public final static class SpecificColumns {
		public final static String version = "VERSION";
	}
	
	/**
	 * 資料庫資料型態
	 *
	 */
	public enum DataType {
		VARCHAR2, NUMBER, NCLOB, DATE, BLOB, LONG, NVARCHAR2, TIMESTAMP, CHAR
	}

	
	DataBaseHandler() {

	}
	
//	public static DataBaseHandler getInstance() {
//		if(handler == null)
//			synchronized (DataBaseHandler.class) {
//				if(handler == null) {
//					handler = new DataBaseHandler();
//				}
//			}
//		return handler;
//	}
	
	public static void main(String[] args) {

	}
	
	public void insert(Connection conn, String schema, String table, Object bean) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sqlColumns = "SELECT TC.COLUMN_NAME, TC.DATA_TYPE, TC.DATA_LENGTH FROM ALL_TAB_COLS TC WHERE TC.OWNER=? AND TC.TABLE_NAME=? ORDER BY TC.COLUMN_ID";
			stmt = conn.prepareStatement(sqlColumns, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, schema);
			stmt.setString(2, table);
			rs = stmt.executeQuery();
			if(!rs.next()) {
				throw new SQLException(DataBaseHandler.errorKey + " " + getClass().getName() + " - table not found:" + schema);
			}
			rs.previous();
			
			doInsert(conn, genInsertStatement(schema, table, rs), rs, bean);
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	abstract void doInsert(Connection conn, String insertStatement, ResultSet rs, Object bean) throws SQLException;
	
	public void update(Connection conn, String schema, String table, Object bean, String... keys) throws SQLException {
		if(null == keys || keys.length == 0) {
			throw new SQLException(DataBaseHandler.errorKey + " updated keys are empty");
		}
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sqlColumns = "SELECT TC.COLUMN_NAME, TC.DATA_TYPE, TC.DATA_LENGTH FROM ALL_TAB_COLS TC WHERE TC.OWNER=? AND TC.TABLE_NAME=? ORDER BY TC.COLUMN_ID";
			stmt = conn.prepareStatement(sqlColumns, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, schema);
			stmt.setString(2, table);
			rs = stmt.executeQuery();
			if(!rs.next()) {
				throw new SQLException(DataBaseHandler.errorKey + " " + getClass().getName() + " - table not found:" + schema);
			}
			rs.previous();
			
			doUpdate(conn, genUpdateStatement(schema, table, rs, bean, keys), rs, bean, keys);
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if(stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	abstract void doUpdate(Connection conn, String updateStatement, ResultSet rs, Object bean, String... keys) throws SQLException;

	public void delete(Connection conn, String schema, String table, Object bean, String... keys) throws Exception {
		doDelete(conn, genDeleteStatement(schema, table, bean, keys), bean, keys);
	}
	
	abstract void doDelete(Connection conn, String deleteStatement, Object bean, String... keys) throws Exception;
	
	/**
	 * SQL to Map
	 */
	public Map<String, String> query(Connection conn, String sql) {
		Map<String,String> data = new HashMap<String,String>();
		Statement stmt = null;
		ResultSet rs = null;
		try{
			logger.debug(sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			if(rs.next()){
				for(int i=1; i<=metaData.getColumnCount(); i++){
					data.put(metaData.getColumnName(i), rs.getString(i));
				}
			}
			metaData = null;
		} catch(SQLException e){
			e.printStackTrace();
		} finally {
			try{
				if(rs != null)
					rs.close();
			} catch(SQLException e){
				e.printStackTrace();
			}
			try{
				if(stmt != null)
					stmt.close();
			} catch(SQLException e){
				e.printStackTrace();
			}			
		}
		return data;
	}

	public void select(Connection conn, String sql) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 串接 insert SQL statement
	 * @param schema
	 * @param table table name
	 * @param rs
	 * @return SQL
	 * @throws SQLException
	 */
	protected String genInsertStatement(String schema, String table, ResultSet rs) throws SQLException {
		StringBuffer insertStatement = new StringBuffer();
		//append statement
		insertStatement.append(" INSERT INTO ").append(schema).append(dot).append(table).append(openParenthesis);
		StringBuffer params = new StringBuffer();
		params.append(openParenthesis);
		while(rs.next()) {
			insertStatement.append(rs.getString(1));
			params.append(questionMark);
			if(!rs.isLast()) {
				insertStatement.append(comma);
				params.append(comma);
			}
			
		}
		params.append(closeParenthesis);
		insertStatement.append(closeParenthesis);
		
		insertStatement.append(" VALUES ").append(params.toString());
		return insertStatement.toString();
	}
	
	/**
	 * 串接 update SQL statement
	 * @param schema
	 * @param table table name
	 * @param rs
	 * @param bean entity
	 * @param keys keys where 條件欄位
	 * @return SQL
	 * @throws SQLException
	 */
	protected String genUpdateStatement(String schema, String table, ResultSet rs, Object bean, String... keys) throws SQLException {
		StringBuffer updateStatement = new StringBuffer();
		// append statement
		updateStatement.append(" UPDATE ").append(schema).append(dot).append(table).append(" SET ");
		
		Class<? extends Object> clazz = bean.getClass();
		try {
			while(rs.next()) {
				try {
					Method m = clazz.getMethod("get" + rs.getString(1));
					// bean value 不為null的欄位會被加入到SQL
					if(null != m.invoke(bean)) {
						updateStatement.append(rs.getString(1)).append(equalTo).append(questionMark);
						updateStatement.append(comma);
					}
					
				} catch (NoSuchMethodException e) {
					logger.error(DataBaseHandler.errorKey + " " + clazz.getName() + " column not found : "
							+ rs.getString(1));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// remove the last comma
			updateStatement.setLength(updateStatement.length() - 1);
			// conditions
			updateStatement.append(" WHERE 1=1");
			for(String key : keys) {
				updateStatement.append(" AND ").append(key).append(equalTo).append(questionMark);
			}
			
		} catch (SQLException e) {
			throw e;
		}
		return updateStatement.toString();
	}
	
	/**
	 * 串接 delete SQL statement
	 * @param schema
	 * @param table table name
	 * @param bean entity
	 * @param keys where 條件欄位
	 * @return SQL
	 * @throws Exception
	 */
	protected String genDeleteStatement(String schema, String table, Object bean, String... keys) throws Exception {
		StringBuffer deleteStatement = new StringBuffer();
		// append statement
		deleteStatement.append(" DELETE ").append(schema).append(dot).append(table).append(" WHERE 1=1");
		
		Class<? extends Object> clazz = bean.getClass();
		for(String key : keys) {
			try {
				Method m = clazz.getMethod("get" + key);
				if(null != m.invoke(bean)) {
					deleteStatement.append(" AND ").append(key).append(equalTo).append(questionMark);
				} else {
					throw new SQLException(DataBaseHandler.errorKey + " " + clazz.getName() + " key value is empty : "
							+ key);
				}
			} catch (NoSuchMethodException e) {
				logger.error(DataBaseHandler.errorKey + " " + clazz.getName() + " delete key not found : "
						+ key);
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
			
		return deleteStatement.toString();
	}
	
	
	protected DataType getDataType(String strType) {
		DataType dataType = null;
		strType = strType == null ? "" : strType;
		try {
			dataType = DataType.valueOf(strType);
		} catch(IllegalArgumentException e) {
			dataType = strType.contains("TIMESTAMP") ? DataType.TIMESTAMP : null; 
		}
		if(dataType == null) {
			logger.error(DataBaseHandler.errorKey + " " + getClass().getName() + "- dataType not found :" + strType);
			dataType = DataType.NVARCHAR2;
		}
		return dataType;
	}
	
	/**
	 * 取得系統現在時間
	 * @return
	 */
	public static java.sql.Timestamp getCurrentDateTime() {
		return new java.sql.Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * Timestamp to String
	 * @param timestamp
	 * @return yyyy-MM-dd
	 */
	public static String format(java.sql.Timestamp timestamp) {
		String dateTime = null;
		try {
			dateTime= dateFormat.format(timestamp);
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		return dateTime;
	}
	
	/**
	 * Timestamp to String
	 * @param timestamp
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String dateTimeformat(java.sql.Timestamp timestamp) {
		String dateTime = null;
		try {
			dateTime= dateTimeFormat.format(timestamp);
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		return dateTime;
	}
	
	/**
	 * Timestamp to String
	 * @param timestamp
	 * @return yyyy/MM/dd
	 */
	public static String slashFormat(java.sql.Timestamp timestamp) {
		String dateTime = null;
		try {
			dateTime= dateSlashFormat.format(timestamp);
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		return dateTime;
	}
	
	/**
	 * Timestamp to String
	 * @param timestamp
	 * @return yyyy/MM/dd HH:mm:ss
	 */
	public static String dateTimeSlashFormat(java.sql.Timestamp timestamp) {
		String dateTime = null;
		try {
			dateTime= dateTimeSlashFormat.format(timestamp);
		} catch(Exception e) {
			e.printStackTrace();
			
		}
		return dateTime;
	}

}
