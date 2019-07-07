package marcus.utils.database;

import java.sql.SQLException;

import com.p6spy.engine.spy.P6PreparedStatement;

/**
 * 定義 利用PreparedStatement 對資料操作的main methods
 * @author marcus.lin
 *
 */
public interface DataInterFace {
	
	public abstract void setString(P6PreparedStatement p6stmt, int index, String columnName, Object bean) throws SQLException;
	
	public abstract void setDate(P6PreparedStatement p6stmt, int index, String columnName, Object bean) throws SQLException;
	
	public abstract void setInt(P6PreparedStatement p6stmt, int index, String columnName, Object bean) throws SQLException;
	
	public abstract void setDouble(P6PreparedStatement p6stmt, int index, String columnName, Object bean) throws SQLException;
	
}
