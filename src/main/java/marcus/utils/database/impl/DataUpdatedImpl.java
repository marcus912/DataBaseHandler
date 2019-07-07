package marcus.utils.database.impl;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.ParseException;

import org.apache.log4j.Logger;

import com.p6spy.engine.spy.P6PreparedStatement;

import marcus.utils.BA_TOOLS;
import marcus.utils.database.DataBaseHandler;
import marcus.utils.database.DataInterFace;

/**
 * 實作 DataInterFace
 * 處理 update 資料
 * 利用 reflection 將物件資料同步至 PreparedStatement
 * @author marcus.lin
 *
 */
public class DataUpdatedImpl implements DataInterFace {
	Logger logger = Logger.getLogger(this.getClass());
	BA_TOOLS tools = BA_TOOLS.getInstance();
	
	public void setString(P6PreparedStatement p6stmt, int index, String columnName, Object bean) throws SQLException {

		String value = "";
		Class<? extends Object> clazz = bean.getClass();
		try {
			String methodName = "get" + columnName;
			value = clazz.getMethod(methodName).invoke(bean) == null ? ""
							: clazz.getMethod(methodName).invoke(bean).toString();

		} catch (NoSuchMethodException e) {
			logger.error(DataBaseHandler.errorKey + " " + clazz.getName() + " column not found : " + columnName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		p6stmt.setString(index, value);

	}

	public void setDate(P6PreparedStatement p6stmt, int index, String columnName, Object bean) throws SQLException{
		Class<? extends Object> clazz = bean.getClass();
		try {
			
			Method method = clazz.getMethod("get" + columnName);
			Class<?> type = method.getReturnType();
			Object obj = method.invoke(bean);
			if(obj == null) {
				p6stmt.setTimestamp(index, null);
				return;
			}
			
			if(type.equals(String.class)) {
				String value = obj == null ? "" : obj.toString();
				if(tools.isEmpty(value)) {
					p6stmt.setDate(index, null);
				} else if(value.toUpperCase().equals(DataBaseHandler.sysDate)) {
					p6stmt.setTimestamp(index, DataBaseHandler.getCurrentDateTime());
				} else {
					try {
						if(value.length() > DataBaseHandler.dateFormat.toPattern().length()) {
							if(value.contains(DataBaseHandler.slash)) {
								p6stmt.setTimestamp(index, new java.sql.Timestamp(DataBaseHandler.dateTimeSlashFormat.parse(value).getTime()));
							} else {
								p6stmt.setTimestamp(index, new java.sql.Timestamp(DataBaseHandler.dateTimeFormat.parse(value).getTime()));
							}
						} else {
							if(value.contains(DataBaseHandler.slash)) {
								p6stmt.setTimestamp(index, new java.sql.Timestamp(DataBaseHandler.dateSlashFormat.parse(value).getTime()));
							} else {
								p6stmt.setTimestamp(index, new java.sql.Timestamp(DataBaseHandler.dateFormat.parse(value).getTime()));
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
						p6stmt.setTimestamp(index, null);
					}
					
				}
			} else if(type.equals(java.sql.Date.class)) {
				p6stmt.setDate(index, (java.sql.Date) obj);
			} else if(type.equals(java.util.Date.class)) {
				p6stmt.setDate(index, new java.sql.Date( ((java.util.Date)obj).getTime()) );
			} else if(type.equals(java.sql.Timestamp.class)) {
				p6stmt.setTimestamp(index, (java.sql.Timestamp) obj);
			} else {
				p6stmt.setTimestamp(index, null);
			}
			
			
		} catch (Exception e) {
			logger.error(clazz.getName() + " column not found : " + columnName);
			p6stmt.setTimestamp(index, null);
		}
	}

	public void setInt(P6PreparedStatement p6stmt, int index, String columnName, Object bean) throws SQLException {

		setString(p6stmt, index, columnName, bean);

	}

	public void setDouble(P6PreparedStatement p6stmt, int index, String columnName, Object bean) throws SQLException {

		setString(p6stmt, index, columnName, bean);

	}

}
