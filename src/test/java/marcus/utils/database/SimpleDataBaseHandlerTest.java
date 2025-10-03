package marcus.utils.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * Test class for SimpleDataBaseHandler
 *
 * Note: These tests are currently ignored because they require Oracle-specific system tables (ALL_TAB_COLS)
 * which are not available in H2 test database. Run these tests against an actual Oracle database.
 */
@Ignore("Requires Oracle database with ALL_TAB_COLS system table")
public class SimpleDataBaseHandlerTest {

	private Connection conn;
	private SimpleDataBaseHandler handler;

	@Before
	public void setUp() throws Exception {
		Class.forName("org.h2.Driver");
		conn = DriverManager.getConnection("jdbc:h2:mem:test2;MODE=Oracle", "sa", "");
		handler = new SimpleDataBaseHandler();

		// Create test table with various data types
		Statement stmt = conn.createStatement();
		stmt.execute("CREATE SCHEMA IF NOT EXISTS TEST_SCHEMA");
		stmt.execute("CREATE TABLE IF NOT EXISTS TEST_SCHEMA.DATA_TYPES_TABLE (" +
				"ID VARCHAR(50) PRIMARY KEY, " +
				"VARCHAR_COL VARCHAR(100), " +
				"NUMBER_COL NUMBER(10,2), " +
				"DATE_COL TIMESTAMP, " +
				"CHAR_COL CHAR(10))");
		stmt.close();
	}

	@After
	public void tearDown() throws Exception {
		if (conn != null && !conn.isClosed()) {
			conn.close();
		}
	}

	@Test
	public void testInsertWithVariousDataTypes() throws Exception {
		TestEntity entity = new TestEntity();
		entity.setID("DT001");
		entity.setVARCHAR_COL("Test String");
		entity.setNUMBER_COL("123.45");
		entity.setDATE_COL(DataBaseHandler.sysDate);
		entity.setCHAR_COL("CHAR10");

		handler.insert(conn, "TEST_SCHEMA", "DATA_TYPES_TABLE", entity);

		// Verify insertion
		Statement stmt = conn.createStatement();
		java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM TEST_SCHEMA.DATA_TYPES_TABLE WHERE ID='DT001'");
		assertTrue("Record should exist", rs.next());
		assertEquals("Test String", rs.getString("VARCHAR_COL"));
		assertEquals(123.45, rs.getDouble("NUMBER_COL"), 0.01);
		assertNotNull(rs.getTimestamp("DATE_COL"));
		rs.close();
		stmt.close();
	}

	@Test
	public void testUpdateWithNullValues() throws Exception {
		// Insert initial record
		Statement stmt = conn.createStatement();
		stmt.execute("INSERT INTO TEST_SCHEMA.DATA_TYPES_TABLE (ID, VARCHAR_COL, NUMBER_COL) " +
				"VALUES ('DT002', 'Original', 100)");
		stmt.close();

		// Update with null values (should not update null fields)
		TestEntity entity = new TestEntity();
		entity.setID("DT002");
		entity.setVARCHAR_COL("Updated");
		// NUMBER_COL is null, should not be updated

		handler.update(conn, "TEST_SCHEMA", "DATA_TYPES_TABLE", entity, "ID");

		// Verify update
		stmt = conn.createStatement();
		java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM TEST_SCHEMA.DATA_TYPES_TABLE WHERE ID='DT002'");
		assertTrue("Record should exist", rs.next());
		assertEquals("Updated", rs.getString("VARCHAR_COL"));
		assertEquals(100.0, rs.getDouble("NUMBER_COL"), 0.01); // Should remain unchanged
		rs.close();
		stmt.close();
	}

	@Test
	public void testInsertWithEmptyStrings() throws Exception {
		TestEntity entity = new TestEntity();
		entity.setID("DT003");
		entity.setVARCHAR_COL("");
		entity.setNUMBER_COL("0");

		handler.insert(conn, "TEST_SCHEMA", "DATA_TYPES_TABLE", entity);

		// Verify insertion
		Statement stmt = conn.createStatement();
		java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM TEST_SCHEMA.DATA_TYPES_TABLE WHERE ID='DT003'");
		assertTrue("Record should exist", rs.next());
		assertEquals("", rs.getString("VARCHAR_COL"));
		rs.close();
		stmt.close();
	}

	/**
	 * Test entity for data types
	 */
	public static class TestEntity {
		private String ID;
		private String VARCHAR_COL;
		private String NUMBER_COL;
		private String DATE_COL;
		private String CHAR_COL;

		public String getID() {
			return ID;
		}

		public void setID(String ID) {
			this.ID = ID;
		}

		public String getVARCHAR_COL() {
			return VARCHAR_COL;
		}

		public void setVARCHAR_COL(String VARCHAR_COL) {
			this.VARCHAR_COL = VARCHAR_COL;
		}

		public String getNUMBER_COL() {
			return NUMBER_COL;
		}

		public void setNUMBER_COL(String NUMBER_COL) {
			this.NUMBER_COL = NUMBER_COL;
		}

		public String getDATE_COL() {
			return DATE_COL;
		}

		public void setDATE_COL(String DATE_COL) {
			this.DATE_COL = DATE_COL;
		}

		public String getCHAR_COL() {
			return CHAR_COL;
		}

		public void setCHAR_COL(String CHAR_COL) {
			this.CHAR_COL = CHAR_COL;
		}
	}
}
