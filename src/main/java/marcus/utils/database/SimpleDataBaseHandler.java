package marcus.utils.database;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.p6spy.engine.spy.P6PreparedStatement;

import marcus.utils.database.impl.DataInsertedImpl;
import marcus.utils.database.impl.DataUpdatedImpl;

/**
 * 實作 DataBaseHandler abstract methods
 * DB資料型態 對應 DataInterFace methods
 * @author marcus.lin
 *
 */
public class SimpleDataBaseHandler extends DataBaseHandler {
	DataInterFace insertImpl = new DataInsertedImpl();
	DataInterFace updateImpl = new DataUpdatedImpl();
	
	@Override
	void doInsert(Connection conn, String insertStatement, ResultSet rs, Object bean) throws SQLException {
		PreparedStatement stmt = null;
		P6PreparedStatement p6stmt = null;
		rs.beforeFirst();
		try {
			stmt = conn.prepareStatement(insertStatement);
			p6stmt = new P6PreparedStatement(null, stmt, null, insertStatement.toString());
			for(int i=1; rs.next(); i++) {
				DataType type = getDataType(rs.getString(2));
				String columnNmae = rs.getString(1);
				switch (type) {
					case BLOB:
						insertImpl.setString(p6stmt, i, columnNmae, bean);
						break;
					case DATE:
						insertImpl.setDate(p6stmt, i, columnNmae, bean);
						break;
					case LONG:
						insertImpl.setInt(p6stmt, i, columnNmae, bean);
						break;
					case NCLOB:
						insertImpl.setString(p6stmt, i, columnNmae, bean);
						break;
					case NUMBER:
						insertImpl.setDouble(p6stmt, i, columnNmae, bean);
						break;
					case NVARCHAR2:
						insertImpl.setString(p6stmt, i, columnNmae, bean);
						break;
					case TIMESTAMP:
						insertImpl.setDate(p6stmt, i, columnNmae, bean);
						break;
					case VARCHAR2:
						insertImpl.setString(p6stmt, i, columnNmae, bean);
						break;
					case CHAR:
						insertImpl.setString(p6stmt, i, columnNmae, bean);
						break;
					default :
						insertImpl.setString(p6stmt, i, columnNmae, bean);
						break;
				}
				
			}
			logger.debug(p6stmt.getQueryFromPreparedStatement());
			stmt.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			if(p6stmt != null)
				p6stmt.close();
			if(stmt != null)
				stmt.close();
		}
		
	}

	@Override
	void doUpdate(Connection conn, String updateStatement, ResultSet rs, Object bean, String... keys) throws SQLException {
		PreparedStatement stmt = null;
		P6PreparedStatement p6stmt = null;
		rs.beforeFirst();
		try {
			Class<? extends Object> clazz = bean.getClass();
			stmt = conn.prepareStatement(updateStatement);
			p6stmt = new P6PreparedStatement(null, stmt, null, updateStatement.toString());
			int i=1;
			while(rs.next()) {
				try {
					Method m = clazz.getMethod("get" + rs.getString(1));
					if(null != m.invoke(bean)) {
						DataType type = getDataType(rs.getString(2));
						if(SpecificColumns.version.equals(rs.getString(1))) {
							continue;
						}
						String columnNmae = rs.getString(1);
						switch (type) {
							case BLOB:
								updateImpl.setString(p6stmt, i, columnNmae, bean);
								break;
							case DATE:
								updateImpl.setDate(p6stmt, i, columnNmae, bean);
								break;
							case LONG:
								updateImpl.setInt(p6stmt, i, columnNmae, bean);
								break;
							case NCLOB:
								updateImpl.setString(p6stmt, i, columnNmae, bean);
								break;
							case NUMBER:
								updateImpl.setDouble(p6stmt, i, columnNmae, bean);
								break;
							case NVARCHAR2:
								updateImpl.setString(p6stmt, i, columnNmae, bean);
								break;
							case TIMESTAMP:
								updateImpl.setDate(p6stmt, i, columnNmae, bean);
								break;
							case VARCHAR2:
								updateImpl.setString(p6stmt, i, columnNmae, bean);
								break;
							case CHAR:
								insertImpl.setString(p6stmt, i, columnNmae, bean);
								break;
							default :
								updateImpl.setString(p6stmt, i, columnNmae, bean);
								break;
						}
						i++;
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
			// conditions
			for(String key : keys) {
				updateImpl.setString(p6stmt, i, key, bean);
				i++;
			}
			
			logger.debug(p6stmt.getQueryFromPreparedStatement());
			stmt.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			if(p6stmt != null)
				p6stmt.close();
			if(stmt != null)
				stmt.close();
		}
		
	}

	@Override
	void doDelete(Connection conn, String deleteStatement, Object bean, String... keys) throws SQLException {
		PreparedStatement stmt = null;
		P6PreparedStatement p6stmt = null;
		try {
			stmt = conn.prepareStatement(deleteStatement);
			p6stmt = new P6PreparedStatement(null, stmt, null, deleteStatement.toString());
			for(int i=0; i < keys.length; i++) {
				updateImpl.setString(p6stmt, i+1, keys[i], bean);
			}
			logger.debug(p6stmt.getQueryFromPreparedStatement());
			stmt.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			if(p6stmt != null)
				p6stmt.close();
			if(stmt != null)
				stmt.close();
		}
		
	}

}
