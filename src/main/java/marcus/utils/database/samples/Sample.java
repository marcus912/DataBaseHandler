package marcus.utils.database.samples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import marcus.utils.database.DataBaseHandler;
import marcus.utils.database.SimpleDataBaseHandler;

public class Sample {
	DataBaseHandler dbHandler = new SimpleDataBaseHandler();

	public static void main(String[] args) {
		Sample sampleClass = new Sample();
		
		Connection conn = null;
		try {
			conn = sampleClass.getConnection();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		CODES entity = sampleClass.genEntity();
		
		// 新增
		sampleClass.addData(conn, entity);
		
		
		// 修改  
		// primary keys 不可異動 
		entity.setCODE_DESC("資料002");
		entity.setCODE_DESC_ENG("data 002");
		entity.setUPDATE_BY("000000000002");
		entity.setUPDATE_DATE(DataBaseHandler.sysDate);
		sampleClass.editData(conn, entity);
		
		// 刪除
		sampleClass.deleteData(conn, entity);
		
		// date format
//		DataBaseHandler dbHandler = new SimpleDataBaseHandler();
//		entity.setUPDATE_DATE(dbHandler.dateTimeSlashFormat(rs.getTimestamp("")));
		
		// 修改指定欄位
		sampleClass.doUpdate(conn);
		
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void doUpdate(Connection conn) {
		String sql = "select code_desc from sc.codes where code_type='SAMPLE' and code='1'";
		CODES code = new CODES();
		code.setCODE_DESC(dbHandler.query(conn, sql).get("CODE_DESC") + "001");
		code.setCODE_DESC_ENG(""); //set null
		// keys
		code.setAPP_NAME("PROG");
		code.setCODE_TYPE("SAMPLE");
		code.setCODE("1");
		editData(conn, code);
	}

	CODES genEntity() {
		CODES codes = new CODES();
		codes.setAPP_NAME("PROG");
		codes.setCODE_TYPE("SAMPLE");
		codes.setCODE("2");
		codes.setCODE_DESC("資料2");
		codes.setCODE_DESC_ENG("data 2");
		codes.setCREATE_BY("000000006776");
		codes.setUPDATE_BY("000000006776");
		codes.setCREATE_DATE(DataBaseHandler.sysDate);
		codes.setUPDATE_DATE(DataBaseHandler.sysDate);
		codes.setVERSION("1");
		return codes;
	}
	
	private void addData(Connection conn, CODES entity) {
		try {
			dbHandler.insert(conn, "SC", "CODES", entity);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void editData(Connection conn, CODES entity) {
		try {
			dbHandler.update(conn, "SC", "CODES", entity, "APP_NAME", "CODE_TYPE", "CODE");
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private void deleteData(Connection conn, CODES entity) {
		try {
			dbHandler.delete(conn, "SC", "CODES", entity, "APP_NAME", "CODE_TYPE", "CODE");
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	Connection getConnection() throws SQLException, ClassNotFoundException {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			throw e;
		}
		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@10.10.2.132:1541:juat", "japps","japps");
			conn.setAutoCommit(false);
			
		} catch (SQLException e) {
			throw e;
		}
		return conn;
	}
	
	
	public class CODES {
		private String APP_NAME;
		private String CODE_TYPE;
		private String CODE;
		private String CODE_DESC;
		private String CODE_DESC_ENG;
		private String CREATE_DATE;
		private String CREATE_BY;
		private String UPDATE_DATE;
		private String UPDATE_BY;
		private String VERSION;
		
		public String getAPP_NAME() {
			return APP_NAME;
		}
		public void setAPP_NAME(String aPP_NAME) {
			APP_NAME = aPP_NAME;
		}
		public String getCODE_TYPE() {
			return CODE_TYPE;
		}
		public void setCODE_TYPE(String cODE_TYPE) {
			CODE_TYPE = cODE_TYPE;
		}
		public String getCODE() {
			return CODE;
		}
		public void setCODE(String cODE) {
			CODE = cODE;
		}
		public String getCODE_DESC() {
			return CODE_DESC;
		}
		public void setCODE_DESC(String cODE_DESC) {
			CODE_DESC = cODE_DESC;
		}
		public String getCODE_DESC_ENG() {
			return CODE_DESC_ENG;
		}
		public void setCODE_DESC_ENG(String cODE_DESC_ENG) {
			CODE_DESC_ENG = cODE_DESC_ENG;
		}
		public String getCREATE_DATE() {
			return CREATE_DATE;
		}
		public void setCREATE_DATE(String cREATE_DATE) {
			CREATE_DATE = cREATE_DATE;
		}
		public String getCREATE_BY() {
			return CREATE_BY;
		}
		public void setCREATE_BY(String cREATE_BY) {
			CREATE_BY = cREATE_BY;
		}
		public String getUPDATE_DATE() {
			return UPDATE_DATE;
		}
		public void setUPDATE_DATE(String uPDATE_DATE) {
			UPDATE_DATE = uPDATE_DATE;
		}
		public String getUPDATE_BY() {
			return UPDATE_BY;
		}
		public void setUPDATE_BY(String uPDATE_BY) {
			UPDATE_BY = uPDATE_BY;
		}
		public String getVERSION() {
			return VERSION;
		}
		public void setVERSION(String vERSION) {
			VERSION = vERSION;
		}
		
		
	}
}
