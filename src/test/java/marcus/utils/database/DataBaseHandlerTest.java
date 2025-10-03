package marcus.utils.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import static org.junit.Assert.*;

/**
 * Test class for DataBaseHandler
 *
 * Note: These tests are currently ignored because they require Oracle-specific system tables (ALL_TAB_COLS)
 * which are not available in H2 test database. Run these tests against an actual Oracle database.
 */
@Ignore("Requires Oracle database with ALL_TAB_COLS system table")
public class DataBaseHandlerTest {

	private Connection conn;
	private DataBaseHandler handler;

	@Before
	public void setUp() throws Exception {
		// Create H2 in-memory database
		Class.forName("org.h2.Driver");
		conn = DriverManager.getConnection("jdbc:h2:mem:test;MODE=Oracle", "sa", "");
		handler = new SimpleDataBaseHandler();

		// Create test table
		Statement stmt = conn.createStatement();
		stmt.execute("CREATE SCHEMA IF NOT EXISTS TEST_SCHEMA");
		stmt.execute("CREATE TABLE IF NOT EXISTS TEST_SCHEMA.TEST_TABLE (" +
				"ID VARCHAR(50) PRIMARY KEY, " +
				"NAME VARCHAR(100), " +
				"DESCRIPTION VARCHAR(255), " +
				"AMOUNT NUMBER(10,2), " +
				"CREATE_DATE TIMESTAMP, " +
				"UPDATE_DATE TIMESTAMP, " +
				"VERSION VARCHAR(10))");
		stmt.close();
	}

	@After
	public void tearDown() throws Exception {
		if (conn != null && !conn.isClosed()) {
			conn.close();
		}
	}

	@Test
	public void testInsert() throws SQLException {
		TestEntity entity = new TestEntity();
		entity.setID("TEST001");
		entity.setNAME("Test Name");
		entity.setDESCRIPTION("Test Description");
		entity.setAMOUNT("100.50");
		entity.setCREATE_DATE(DataBaseHandler.sysDate);
		entity.setUPDATE_DATE(DataBaseHandler.sysDate);
		entity.setVERSION("1");

		handler.insert(conn, "TEST_SCHEMA", "TEST_TABLE", entity);

		// Verify insertion
		Statement stmt = conn.createStatement();
		java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM TEST_SCHEMA.TEST_TABLE WHERE ID='TEST001'");
		assertTrue("Record should exist", rs.next());
		assertEquals("Test Name", rs.getString("NAME"));
		assertEquals("Test Description", rs.getString("DESCRIPTION"));
		rs.close();
		stmt.close();
	}

	@Test
	public void testUpdate() throws SQLException {
		// Insert initial record
		Statement stmt = conn.createStatement();
		stmt.execute("INSERT INTO TEST_SCHEMA.TEST_TABLE (ID, NAME, DESCRIPTION, AMOUNT, VERSION) " +
				"VALUES ('TEST002', 'Original Name', 'Original Desc', 50.00, '1')");
		stmt.close();

		// Update the record
		TestEntity entity = new TestEntity();
		entity.setID("TEST002");
		entity.setNAME("Updated Name");
		entity.setDESCRIPTION("Updated Description");
		entity.setAMOUNT("75.25");

		handler.update(conn, "TEST_SCHEMA", "TEST_TABLE", entity, "ID");

		// Verify update
		stmt = conn.createStatement();
		java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM TEST_SCHEMA.TEST_TABLE WHERE ID='TEST002'");
		assertTrue("Record should exist", rs.next());
		assertEquals("Updated Name", rs.getString("NAME"));
		assertEquals("Updated Description", rs.getString("DESCRIPTION"));
		rs.close();
		stmt.close();
	}

	@Test
	public void testUpdateWithMultipleKeys() throws SQLException {
		// Insert initial record
		Statement stmt = conn.createStatement();
		stmt.execute("INSERT INTO TEST_SCHEMA.TEST_TABLE (ID, NAME, DESCRIPTION, VERSION) " +
				"VALUES ('TEST003', 'Test', 'Description', '1')");
		stmt.close();

		// Update using multiple keys
		TestEntity entity = new TestEntity();
		entity.setID("TEST003");
		entity.setNAME("Updated with Multiple Keys");
		entity.setVERSION("1");

		handler.update(conn, "TEST_SCHEMA", "TEST_TABLE", entity, "ID", "VERSION");

		// Verify update
		stmt = conn.createStatement();
		java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM TEST_SCHEMA.TEST_TABLE WHERE ID='TEST003'");
		assertTrue("Record should exist", rs.next());
		assertEquals("Updated with Multiple Keys", rs.getString("NAME"));
		rs.close();
		stmt.close();
	}

	@Test
	public void testDelete() throws Exception {
		// Insert initial record
		Statement stmt = conn.createStatement();
		stmt.execute("INSERT INTO TEST_SCHEMA.TEST_TABLE (ID, NAME, VERSION) " +
				"VALUES ('TEST004', 'To Delete', '1')");
		stmt.close();

		// Delete the record
		TestEntity entity = new TestEntity();
		entity.setID("TEST004");

		handler.delete(conn, "TEST_SCHEMA", "TEST_TABLE", entity, "ID");

		// Verify deletion
		stmt = conn.createStatement();
		java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM TEST_SCHEMA.TEST_TABLE WHERE ID='TEST004'");
		assertFalse("Record should not exist", rs.next());
		rs.close();
		stmt.close();
	}

	@Test(expected = SQLException.class)
	public void testUpdateWithoutKeys() throws SQLException {
		TestEntity entity = new TestEntity();
		entity.setID("TEST005");
		entity.setNAME("Test");

		// Should throw SQLException because no keys provided
		handler.update(conn, "TEST_SCHEMA", "TEST_TABLE", entity);
	}

	@Test
	public void testGetCurrentDateTime() {
		Timestamp timestamp = DataBaseHandler.getCurrentDateTime();
		assertNotNull("Timestamp should not be null", timestamp);
		assertTrue("Timestamp should be recent",
				System.currentTimeMillis() - timestamp.getTime() < 1000);
	}

	@Test
	public void testFormatTimestamp() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String formatted = DataBaseHandler.format(timestamp);
		assertNotNull("Formatted date should not be null", formatted);
		assertTrue("Formatted date should match pattern", formatted.matches("\\d{4}-\\d{2}-\\d{2}"));
	}

	@Test
	public void testDateTimeFormat() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String formatted = DataBaseHandler.dateTimeformat(timestamp);
		assertNotNull("Formatted datetime should not be null", formatted);
		assertTrue("Formatted datetime should match pattern",
				formatted.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
	}

	/**
	 * Test entity class
	 */
	public static class TestEntity {
		private String ID;
		private String NAME;
		private String DESCRIPTION;
		private String AMOUNT;
		private String CREATE_DATE;
		private String UPDATE_DATE;
		private String VERSION;

		public String getID() {
			return ID;
		}

		public void setID(String ID) {
			this.ID = ID;
		}

		public String getNAME() {
			return NAME;
		}

		public void setNAME(String NAME) {
			this.NAME = NAME;
		}

		public String getDESCRIPTION() {
			return DESCRIPTION;
		}

		public void setDESCRIPTION(String DESCRIPTION) {
			this.DESCRIPTION = DESCRIPTION;
		}

		public String getAMOUNT() {
			return AMOUNT;
		}

		public void setAMOUNT(String AMOUNT) {
			this.AMOUNT = AMOUNT;
		}

		public String getCREATE_DATE() {
			return CREATE_DATE;
		}

		public void setCREATE_DATE(String CREATE_DATE) {
			this.CREATE_DATE = CREATE_DATE;
		}

		public String getUPDATE_DATE() {
			return UPDATE_DATE;
		}

		public void setUPDATE_DATE(String UPDATE_DATE) {
			this.UPDATE_DATE = UPDATE_DATE;
		}

		public String getVERSION() {
			return VERSION;
		}

		public void setVERSION(String VERSION) {
			this.VERSION = VERSION;
		}
	}
}
