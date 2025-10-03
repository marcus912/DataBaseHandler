# DataBaseHandler

A lightweight ORM library that simplifies database operations using Java Reflection, eliminating the need to write repetitive SQL statements.

## Features

- **Simple CRUD Operations**: Insert, update, delete without writing SQL
- **Reflection-Based Mapping**: Automatic mapping between Java objects and database tables
- **Minimal Configuration**: No complex setup or annotations required
- **JDBC Compatible**: Works with any JDBC-compliant database

## Quick Start

```java
// Establish database connection
Connection conn = DriverManager.getConnection("url", "username", "password");
DataBaseHandler dbHandler = new SimpleDataBaseHandler();

// Create and populate entity
CODES entity = new CODES();
entity.setCODE_DESC("description");
entity.setCODE_DESC_ENG("data 002");
entity.setUPDATE_BY("000000000002");
entity.setUPDATE_DATE(DataBaseHandler.sysDate);

// Update record (no SQL required!)
dbHandler.update(conn, "SC", "CODES", entity, "APP_NAME", "CODE_TYPE", "CODE");
```

## How to Use

### Step 1: Create Entity Class

Create an entity class for Object-Relational Mapping (ORM). **Important**: Class field names must exactly match the database table column names.

```java
public class CODES {
    private String APP_NAME;
    private String CODE_TYPE;
    private String CODE;
    private String CODE_DESC;
    // ... getters and setters
}
```

### Step 2: Populate Entity

Assign values to your entity object.

```java
CODES entity = new CODES();
entity.setAPP_NAME("PROG");
entity.setCODE_TYPE("SAMPLE");
entity.setCODE("1");
```

### Step 3: Execute Operations

Call the appropriate DataBaseHandler method:

```java
// Insert
dbHandler.insert(conn, "SCHEMA", "TABLE_NAME", entity);

// Update (specify primary key columns)
dbHandler.update(conn, "SCHEMA", "TABLE_NAME", entity, "KEY_COLUMN_1", "KEY_COLUMN_2");

// Delete
dbHandler.delete(conn, "SCHEMA", "TABLE_NAME", entity, "KEY_COLUMN_1", "KEY_COLUMN_2");
```

For complete examples, see [Sample.java](src/main/java/marcus/utils/database/samples/Sample.java).

## Why Not Hibernate?

This library was created in 2017 for a legacy Struts 1 project that relied heavily on JDBC. Developers were spending significant time writing SQL statements, especially for complex tables.

DataBaseHandler provides a lightweight, easy-to-learn alternative that:
- Requires minimal setup
- Works with existing JDBC code
- Has a small footprint
- Is simple to integrate into legacy projects

## Requirements

- Java 8 or higher
- JDBC-compliant database driver
- Apache Log4j (for logging)
