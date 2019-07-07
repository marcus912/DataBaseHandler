package marcus.utils.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * 定義main methods
 * @author marcus.lin
 *
 */
public interface DataBaseActionInterFace {
	/**
	 * insert
	 * @param conn
	 * @param schema
	 * @param table table name
	 * @param bean entity
	 * @throws SQLException
	 */
	public void insert(Connection conn, String schema, String table, Object bean) throws SQLException;
	
	/**
	 * update <br>
	 * entity set 空字串 可清空DB欄位(null) <br>
	 * skip entity 值為 null 的欄位 <br>
	 * @param conn
	 * @param schema
	 * @param table table name
	 * @param bean entity
	 * @param keys primary keys
	 * @throws SQLException
	 */
	public void update(Connection conn, String schema, String table, Object bean, String... keys) throws SQLException;
	
	/**
	 * 
	 * @param conn
	 * @param schema
	 * @param table table name
	 * @param bean entity
	 * @param keys primary keys
	 * @throws Exception
	 */
	public void delete(Connection conn, String schema, String table, Object bean, String... keys) throws Exception;
	
	public Map<String,String> query(Connection conn, String sql) throws SQLException;
	
	public void select(Connection conn, String sql) throws SQLException;
}
