# DataBaseHandler Tests

## Overview

This test suite provides unit tests for the DataBaseHandler library.

## Current Test Coverage

### DataTypeTest
Tests the DataType enum and type detection functionality:
- ✅ VARCHAR2, NUMBER, DATE, TIMESTAMP, BLOB, NCLOB, CHAR types
- ✅ Timestamp with precision handling
- ✅ Unknown type fallback
- ✅ Null handling

Tests run: 10, Failures: 0, Errors: 0

## Known Limitations

The full integration tests (DataBaseHandlerTest and SimpleDataBaseHandlerTest) currently fail because:

1. **Oracle-specific dependencies**: The DataBaseHandler queries Oracle system tables (`ALL_TAB_COLS`) to introspect table metadata
2. **H2 compatibility**: H2 database (used for testing) does not have Oracle's system tables even in Oracle compatibility mode

## Future Improvements

To enable full integration testing:

1. **Option 1 - Mock approach**: Use Mockito to mock the metadata ResultSet
2. **Option 2 - Oracle testcontainers**: Use Testcontainers with actual Oracle database
3. **Option 3 - Refactor**: Extract metadata query logic to allow custom implementations per database vendor

## Running Tests

```bash
# Run all tests (DataTypeTest will pass)
mvn test

# Run specific test class
mvn test -Dtest=DataTypeTest
```

##Manual Testing

For integration testing, use the Sample class with an actual Oracle database connection:
```
src/main/java/marcus/utils/database/samples/Sample.java
```
