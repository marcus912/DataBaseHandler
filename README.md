# DataBaseHandler
The purpose is to avoid accessing database through PL/SQL.

It's based on Java Reflection.

<pre>
<code>
Connection conn = conn = DriverManager.getConnection("url","name","password");
DataBaseHandler dbHandler = new SimpleDataBaseHandler();

CODES entity = new CODES;
entity.setCODE_DESC("description");
entity.setCODE_DESC_ENG("data 002");
entity.setUPDATE_BY("000000000002");
entity.setUPDATE_DATE(DataBaseHandler.sysDate);

// update(Connection conn, String schema, String table, Object bean, String... keys)
dbHandler.update(conn, "SC", "CODES", entity, "APP_NAME", "CODE_TYPE", "CODE");
</code>
</pre>

## **How to use it ?**

<b>step 1 </b>

Create a entity for Object-relational mapping (ORM).

Make sure there are no differences between class variables and table columns.

<b>step 2 </b>

Assign values to entity

<b>step 3 </b> <br/>

Call DataBaseHandler update function, data will be stored in database without update statement.

https://github.com/marcus912/tools/blob/master/src/main/java/marcus/utils/database/samples/Sample.java

<b>Conclusion </b>

You might ask why don't we just use hibernate ?

In 2017, we got a project which was an enhancement for previous project. The previous project was developed based on struts1 and used to access database through JDBC. Developers had spent a lot of time on coding statements specially when they were accessing complex tables.

I programmed it in order to resolve this issue. It's a light library and easy to use.
