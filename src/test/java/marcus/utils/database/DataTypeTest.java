package marcus.utils.database;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for DataType enum and related functionality
 */
public class DataTypeTest {

	private DataBaseHandler handler = new SimpleDataBaseHandler();

	@Test
	public void testGetDataTypeVarchar2() {
		DataBaseHandler.DataType type = handler.getDataType("VARCHAR2");
		assertEquals(DataBaseHandler.DataType.VARCHAR2, type);
	}

	@Test
	public void testGetDataTypeNumber() {
		DataBaseHandler.DataType type = handler.getDataType("NUMBER");
		assertEquals(DataBaseHandler.DataType.NUMBER, type);
	}

	@Test
	public void testGetDataTypeDate() {
		DataBaseHandler.DataType type = handler.getDataType("DATE");
		assertEquals(DataBaseHandler.DataType.DATE, type);
	}

	@Test
	public void testGetDataTypeTimestamp() {
		DataBaseHandler.DataType type = handler.getDataType("TIMESTAMP");
		assertEquals(DataBaseHandler.DataType.TIMESTAMP, type);
	}

	@Test
	public void testGetDataTypeTimestampWithPrecision() {
		// Should handle TIMESTAMP(6) format
		DataBaseHandler.DataType type = handler.getDataType("TIMESTAMP(6)");
		assertEquals(DataBaseHandler.DataType.TIMESTAMP, type);
	}

	@Test
	public void testGetDataTypeBlob() {
		DataBaseHandler.DataType type = handler.getDataType("BLOB");
		assertEquals(DataBaseHandler.DataType.BLOB, type);
	}

	@Test
	public void testGetDataTypeNClob() {
		DataBaseHandler.DataType type = handler.getDataType("NCLOB");
		assertEquals(DataBaseHandler.DataType.NCLOB, type);
	}

	@Test
	public void testGetDataTypeChar() {
		DataBaseHandler.DataType type = handler.getDataType("CHAR");
		assertEquals(DataBaseHandler.DataType.CHAR, type);
	}

	@Test
	public void testGetDataTypeUnknown() {
		// Should default to NVARCHAR2 for unknown types
		DataBaseHandler.DataType type = handler.getDataType("UNKNOWN_TYPE");
		assertEquals(DataBaseHandler.DataType.NVARCHAR2, type);
	}

	@Test
	public void testGetDataTypeNull() {
		// Should handle null gracefully
		DataBaseHandler.DataType type = handler.getDataType(null);
		assertEquals(DataBaseHandler.DataType.NVARCHAR2, type);
	}

	@Test
	public void testGetDataTypeCaseInsensitive() {
		// getDataType converts to uppercase internally via valueOf
		// But if it fails, it returns NVARCHAR2 as default
		DataBaseHandler.DataType type = handler.getDataType("VARCHAR2");
		assertEquals(DataBaseHandler.DataType.VARCHAR2, type);
	}
}
