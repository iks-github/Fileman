package de.iksgmbh.dbschemacomp.helper;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import de.iksgmbh.dbschemacomp.domain.SchemaMetaData;
import de.iksgmbh.dbschemacomp.util.TestUtil;

/**
 * Most basic Unit tests in this package!
 * 
 * @author Reik Oberrath
 */
public class SchemaParserTest 
{
	@Test
	public void parsesSimpleSchemaWithWhiteSpaces() 
	{
		// arrange
		String schema = " CREATE  TABLE table_name " + System.getProperty("line.separator")
				        + " ( column1  datatype1  ,  "
				        + "   column2	datatype2 ) ";
		
		// act
		SchemaMetaData result = SchemaParser.doYourJob(schema);
		
		// assert
		assertNotNull(result);
		assertEquals(1, result.getTableNumber(), "Number of Tables");
		assertEquals(2, result.getTable(1).getColumnNumber(), "Number of Columns");
		assertEquals("column1", result.getTable(1).getColumn(1).getColumnName().getValue(), "Column Name");
		assertEquals("datatype1", result.getTable(1).getColumn(1).getColumnType().getValue(), "Column Type");
		assertEquals("column2", result.getTable(1).getColumn(2).getColumnName().getValue(), "Column Name");
		assertEquals("datatype2", result.getTable(1).getColumn(2).getColumnType().getValue(), "Column Type");
	}

	@Test
	public void parsesSchemaWithTwoTables() 
	{
		// arrange
		String schema = "CREATE TABLE table1 (column11 datatype11, column12 datatype12, column13 datatype13);"
				      + "create table table2 (column21 datatype21);";
		
		// act
		SchemaMetaData result = SchemaParser.doYourJob(schema);
		
		// assert
		assertNotNull(result);
		assertEquals(2, result.getTableNumber(), "Number of Tables");
		assertEquals(3, result.getTable(1).getColumnNumber(), "Number of Columns");
		assertEquals(1, result.getTable(2).getColumnNumber(), "Number of Columns");
		assertEquals("column12", result.getTable(1).getColumn(2).getColumnName().getValue(), "Column Name");
		assertEquals("datatype12", result.getTable(1).getColumn(2).getColumnType().getValue(), "Column Type");
		assertEquals("column21", result.getTable(2).getColumn(1).getColumnName().getValue(), "Column Name");
		assertEquals("datatype21", result.getTable(2).getColumn(1).getColumnType().getValue(), "Column Type");
	}
	
	@Test
	public void parsesSchemaWithNotNullColumn() 
	{	
		// arrange
		String schema = "CREATE TABLE table1 (column1 varchar null,"
				                           + "column2 varchar not null,"
				                           + "column3 varchar);";
		
		// act
		SchemaMetaData result = SchemaParser.doYourJob(schema);
		
		// assert
		assertEquals(true, result.getTable(1).getColumn(1).isNullable(), "is column nullable");
		assertEquals(false, result.getTable(1).getColumn(2).isNullable(), "is column nullable");
		assertEquals(true, result.getTable(1).getColumn(3).isNullable(), "is column nullable");
	}
	
	@Test
	public void throwsExceptionForInvalidNullableState() 
	{
		// arrange
		String schema = "CREATE TABLE table (column1 datatype nullable);";
	
		try {
			// act
			SchemaParser.doYourJob(schema);
			fail("Expected exception not thrown!");
		} catch (Exception e) {
			// assert
			assertEquals("Invalid column definition: (column1 datatype nullable)", 
					     e.getMessage(), "Error Message");
		}
	
	}
	

	@Test
	public void parsesSchemaWithDefaultValue() 
	{	
		// arrange
		String schema = "CREATE TABLE table1 (column1 varchar default value,"
				                           + "column2 varchar);";
		
		// act
		SchemaMetaData result = SchemaParser.doYourJob(schema);
		
		// assert
		assertEquals("value", result.getTable(1).getColumn(1).getDefault(), "Default Value");
		assertEquals(null, result.getTable(1).getColumn(2).getDefault(), "Default Value");
	}
	
	@Test
	public void parsesSchemaWithNotNullAndDefaultValue() 
	{	
		// arrange
		String schema = "CREATE TABLE table1 (column1 varchar default value not null);";
		
		// act
		SchemaMetaData result = SchemaParser.doYourJob(schema);
		
		// assert
		assertEquals("value", result.getTable(1).getColumn(1).getDefault(), "Default Value");
		assertEquals(false, result.getTable(1).getColumn(1).isNullable(), "is column nullable");
	}
		
	@Test
	public void parsesSchemaWithGenerationInfo() 
	{	
		// arrange
		String schema = "CREATE TABLE tablename (ID int generated by default as identity, INFO varchar);";
		
		// act
		SchemaMetaData result = SchemaParser.doYourJob(schema);
		
		// assert
		assertEquals("generated by default as identity", result.getTable(1).getColumn(1).getGenerationInfo(), "Generation Info");
		assertEquals(null, result.getTable(1).getColumn(2).getGenerationInfo(), "Generation Info");
	}
	
	@Test
	public void parsesSchemaWithPrimaryKey() 
	{
		// arrange
		String schema = " CREATE  TABLE table_name " + System.getProperty("line.separator")
				        + " (column1 datatype1,  "
				        + "  column2 datatype2, primary key (column1));";
		
		// act
		SchemaMetaData result = SchemaParser.doYourJob(schema);
		
		// assert
		assertEquals("(column1)", result.getTable(1).getPrimaryKey(), "Primary Key");
	}

	@Test
	public void parsesSchemaWithCompositePrimaryKey() 
	{
		// arrange
		String schema = " CREATE  TABLE table_name " + System.getProperty("line.separator")
				        + " (column1 datatype1, column2 datatype1, "
				        + "  column3 datatype2, primary key (column1, column3));";
		
		// act
		SchemaMetaData result = SchemaParser.doYourJob(schema);
		
		// assert
		assertEquals("(column1, column3)", result.getTable(1).getPrimaryKey(), "Primary Key");
	}
	
	@Test
	public void parsesSchemaWithUniqueConstraint() 
	{
		// arrange
		String schema = "create table aTable (aColumn aColumnType, anotherColumn aColumnType);"
				      + "alter table aTable add constraint anId unique (aColumn);"
		              + "alter table aTable add constraint anotherId unique (anotherColumn);";
		
		// act
		SchemaMetaData result = SchemaParser.doYourJob(schema);
		
		// assert
		assertEquals(2, result.getTable(1).getAddConstraintStatements().size(), "number of new constraints");		
		assertEquals("ALTER TABLE ATABLE ADD CONSTRAINT ANID UNIQUE (ACOLUMN)", 
					     result.getTable(1).getAddConstraintStatements().get(0), "new constraint statement");
		assertEquals("ALTER TABLE ATABLE ADD CONSTRAINT ANOTHERID UNIQUE (ANOTHERCOLUMN)", 
				     result.getTable(1).getAddConstraintStatements().get(1), "new constraint statement");
	}

	@Test
	public void parsesSchemaWithForeignKeyConstraint() 
	{
		// arrange alter table user_tenant add constraint FKb3epikbu8jwurpv4xcy5kpcmm foreign key (fk_user) references USER
		String schema = "create table aTable (aColumn aColumnType, anotherColumn aColumnType);"
				      + "alter table aTable add constraint anId foreign key (anotherColumn) references anotherTable(aColumn);";
		
		// act
		SchemaMetaData result = SchemaParser.doYourJob(schema);
		
		// assert
		assertEquals(1, result.getTable(1).getAddConstraintStatements().size(), "number of new constraints");		
		assertEquals("ALTER TABLE ATABLE ADD CONSTRAINT ANID FOREIGN KEY (ANOTHERCOLUMN) REFERENCES ANOTHERTABLE(ACOLUMN)", 
				     result.getTable(1).getAddConstraintStatements().get(0), "new constraint statement");
	}
	
	
	
	
	@Test
	public void throwsExceptionForUnknownTable() 
	{
		// arrange
		String schema = "alter table aTable add constraint anId unique (aColumn);";
		
		try {
			// act
			SchemaParser.doYourJob(schema);
			fail("Expected exception not thrown!");
		} catch (Exception e) {
			// assert
			assertEquals("Unknown Table in Alter-Statement: "
					     + "ALTER TABLE ATABLE ADD CONSTRAINT ANID UNIQUE (ACOLUMN)", 
					     e.getMessage(), "Error Message");
		}
	}
	
	@Test
	public void throwsExceptionForUnknownColumn() 
	{
		// arrange
		String schema = "create table aTable (Column1 aColumnType);"
			           + "alter table aTable add constraint anId unique (Column2);";
		try {
			// act
			SchemaParser.doYourJob(schema);
			fail("Expected exception not thrown!");
		} catch (Exception e) {
			// assert
			assertEquals("Unknown column 'COLUMN2' in Alter-Statement for table 'ATABLE'.", 
					     e.getMessage(), "Error Message");
		}
	}	
	
	@Test
	public void parsesH2Schema() 
	{
		// arrange
		String schema = TestUtil.readSchema("src/test/resources/H2DbSchema1.txt");
		//System.err.println(schema);
		
		// act
		SchemaMetaData result = SchemaParser.doYourJob(schema);
		
		// assert
		assertEquals(6, result.getTableNumber(), "Number of Tables");
		assertEquals("(creation_date)", result.getTable(1).getPrimaryKey(), "Primary Key");
		assertEquals("generated by default as identity", result.getTable(2).getColumn(1).getGenerationInfo(), "Generation Info");
		assertEquals(true, result.getTable(3).getColumn(2).isNullable(), "is column nullable");
		assertEquals(null, result.getTable(4).getColumn(1).getGenerationInfo(), "Generation Info");
		assertEquals(null, result.getTable(4).getColumn(1).getDefault(), "Default Value");
		assertEquals("ALTER TABLE USER ADD CONSTRAINT UK_A65G3FMTVDWJ8RO965B14FNKO UNIQUE (NAME)", 
				     result.getTable(5).getAddConstraintStatements().get(0), "Unique Constraint Statement");
		assertEquals(false, result.getTable(6).getColumn(1).isNullable(), "is column nullable");
		assertEquals(4, result.getTable(6).getColumnNumber(), "Number of Columns");
	}

	@Test
	public void unifiesSchema() throws SQLException, IOException 
	{
		// arrange
		String schema = FileUtil.readTextFile("src/test/resources/H2DbSchema2.txt");

		// act
		String result = SchemaParser.unify(schema).replaceAll(";", ";" + System.getProperty("line.separator")).trim();
		
		// assert
		assertEquals(FileUtil.readTextFile("src/test/resources/ExpectedUnifiedResult.txt").trim(), result, "Unified schema");
	}	
}
