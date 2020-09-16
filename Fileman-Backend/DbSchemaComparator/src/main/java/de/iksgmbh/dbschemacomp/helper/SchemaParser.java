package de.iksgmbh.dbschemacomp.helper;

import java.util.List;

import de.iksgmbh.dbschemacomp.domain.ColumnMetaData;
import de.iksgmbh.dbschemacomp.domain.Name;
import de.iksgmbh.dbschemacomp.domain.SchemaMetaData;
import de.iksgmbh.dbschemacomp.domain.SqlConstants;
import de.iksgmbh.dbschemacomp.domain.TableMetaData;
import de.iksgmbh.dbschemacomp.domain.Type;

public class SchemaParser extends SqlConstants
{
	private static final String DEFAULT_IDENTIFIER = "default ";
	private static final String UNIQUE = "unique";
	private static final String GENERATION_INFO_IDENTIFIER = "generated by default as ";
	private static final String PRIMARY_KEY_IDENTIFIER = "primary key ";
	private static final List<String> SQL_COMMANDS = List.of(CREATE_TABLE_IDENTIFIER,
			                                                 ALTER_TABLE_IDENTIFIER);
	
	private String origSchema;
	private String notYetParsed;
	private SchemaMetaData metaData = new SchemaMetaData();
	private String statementToParse;

	private SchemaParser(String schemaSql) {
		origSchema = schemaSql;
	}
	
	public static SchemaMetaData doYourJob(String schemaSql) 
	{
		return new SchemaParser(schemaSql).parse();
	}

	private String removeWhiteSpace(String schemaSql) 
	{
		StringBuffer sb = new StringBuffer(schemaSql);
		SQL_COMMANDS.forEach(command -> addSemicolonIfNeeded(command, sb));
		schemaSql = sb.toString();
		schemaSql = schemaSql.replaceAll("\n", "").replaceAll("\r", "").trim();
		if (schemaSql.startsWith(";")) schemaSql = schemaSql.substring(1);

		while (schemaSql.contains("  ")) {
			schemaSql = schemaSql.replaceAll("\t", " ")
					             .replace(System.getProperty("line.separator"), " ")
					             .replaceAll("  ", " ");
		}
		return schemaSql;
	}

	private void addSemicolonIfNeeded(String command, StringBuffer sb) {
		String s = sb.toString();
		sb.delete(0, s.length());
		s = s.replaceAll(command, ";"+command);
		sb.append(s);
	}

	private SchemaMetaData parse() 
	{
		notYetParsed = removeWhiteSpace(origSchema).trim();
		
		while (notYetParsed.length() > 0) {
			parseNextStatement(notYetParsed);
		}
		
		return metaData;
	}

	private void parseNextStatement(String toParse) 
	{	
		int pos = toParse.indexOf(";");
		
		if (pos == -1) {
			statementToParse = toParse.trim();
			notYetParsed = "";
		} else {			
			statementToParse = toParse.substring(0, pos).trim();
			notYetParsed = toParse.substring(pos+1);
		}
		
		if (statementToParse.length() > 0) {			
			parseStatement(statementToParse);
		}
	}

	private void parseStatement(String statement) 
	{
		if (statement.toLowerCase().startsWith(CREATE_TABLE_IDENTIFIER.toLowerCase())) {
			parseCreateStatement(statement);
			return;
		}
		
		if (statement.toLowerCase().startsWith(ALTER_TABLE_IDENTIFIER.toLowerCase())) {
			parseAlterStatement(statement);
			return;
		}
		
		throw new RuntimeException("Unknown statement type: " + statement);
	}

	private void parseAlterStatement(String statement) 
	{
		String statementRest = statement.substring(CREATE_TABLE_IDENTIFIER.length()).trim();
		int pos = statementRest.indexOf(" ");
		String tableName = statementRest.substring(0, pos);
		TableMetaData table = metaData.getTable(tableName);
		
		if (table == null) {
			throw new RuntimeException("Unknown Table in Alter-Statement: " + statement);
		}
		
		statementRest = statementRest.substring(tableName.length()).trim();
		parseAlterType(statementRest, table);
	}

	private void parseAlterType(String statement, TableMetaData table) 
	{
		if (statement.toLowerCase().startsWith(ALTER_TABLE_TYPE_ADD_CONSTRAINT.toLowerCase())
			&& statement.toLowerCase().contains(UNIQUE)	) 
		{
			parseUniqueConstraintStatement(statement, table);
			return;
		}
		
		throw new RuntimeException("Unknown Alter-Table-Type: " + statement);
	}

	private void parseUniqueConstraintStatement(String statement, TableMetaData table) 
	{
		String constraintAsString = statement.substring(ALTER_TABLE_TYPE_ADD_CONSTRAINT.length()).trim();
		String[] splitResult = constraintAsString.split(" ");
		if (splitResult.length != 3) {
			throw new RuntimeException("Invalid unique constraint statement: " + statement);
		}
		String columns = cutEmbracingBrackets(splitResult[2]);
		ColumnMetaData column = table.getColumn(columns);
		if (column == null) {
			throw new RuntimeException("Unknown column '" + columns 
					                  + "' in Alter-Statement for table '" + table.getTableName() + "'.");
		}
		table.setUniqueConstraintStatement(statementToParse);
	}

	private String cutEmbracingBrackets(String s) 
	{
		if (s.startsWith("(") && s.endsWith(")")) {
			return s.substring(1, s.length()-1);
		}
		throw new RuntimeException("Invalid column definition in unique constraint: " + s);
	}

	private void parseCreateStatement(String statement) 
	{
		String statementRest = statement.substring(CREATE_TABLE_IDENTIFIER.length()).trim();
		int pos = statementRest.indexOf(" ");
		TableMetaData tableMetaData = new TableMetaData(statementRest.substring(0, pos), statement);
		metaData.addTable(tableMetaData);
		
		parseColumnsMetaData(tableMetaData, statementRest.substring(pos).trim());
		
	}

	private void parseColumnsMetaData(TableMetaData tableMetaData, String s) 
	{
		String[] splitResult = s.split(",");
		for (String tableDefinitionPart : splitResult) 
		{
			tableDefinitionPart = tableDefinitionPart.trim();
			if (tableDefinitionPart.startsWith(PRIMARY_KEY_IDENTIFIER)) {
				parsePrimaryKey(tableMetaData, tableDefinitionPart);
			} else {
				tableDefinitionPart = parseColumnData(tableMetaData, tableDefinitionPart);
			}
		}
	}

	private void parsePrimaryKey(TableMetaData tableMetaData, String tableDefinitionPart) 
	{
		String primaryKey = tableDefinitionPart.substring(PRIMARY_KEY_IDENTIFIER.length());

		if (primaryKey.endsWith(")")) {
			primaryKey = primaryKey.substring(0, primaryKey.length()-1);
		}
		primaryKey = primaryKey.trim();
		tableMetaData.setPrimaryKey(primaryKey);
	}

	private String parseColumnData(TableMetaData tableMetaData, 
			                       String columnData) 
	{
        String columnDataRest = columnData;

		if (columnDataRest.startsWith("(")) {
			columnDataRest = columnDataRest.substring(1);
		}
		if (columnDataRest.endsWith(")")) {
			columnDataRest = columnDataRest.substring(0, columnDataRest.length()-1);
		}
		columnDataRest = columnDataRest.trim();
		
		Boolean isColumnNullable = null;
		if (columnDataRest.contains("not null")) {
			isColumnNullable = false;
			columnDataRest = columnDataRest.replace("not null", "").replace("  ", " ");
		}
		if (columnDataRest.contains("null")) {
			isColumnNullable = true;
			columnDataRest = columnDataRest.replace("null", "").replace("  ", " ");
		}
		columnDataRest = columnDataRest.trim();

		String generationInfo = null;
		if (columnDataRest.contains(GENERATION_INFO_IDENTIFIER)) {
			int pos = columnDataRest.indexOf(GENERATION_INFO_IDENTIFIER) + GENERATION_INFO_IDENTIFIER.length();
			String tmp = columnDataRest.substring(pos).trim();
			pos = tmp.indexOf(" ");
			generationInfo = tmp;
			if (pos > -1) {
				generationInfo = tmp.substring(0, pos);
			}
			generationInfo = GENERATION_INFO_IDENTIFIER + generationInfo;
			columnDataRest = columnDataRest.replace(generationInfo, "")
					               .replace("  ", " ");
		}
		columnDataRest = columnDataRest.trim();
		
		String defaultValue = null;
		if (columnDataRest.contains(DEFAULT_IDENTIFIER)) {
			int pos = columnDataRest.indexOf(DEFAULT_IDENTIFIER) + DEFAULT_IDENTIFIER.length();
			String tmp = columnDataRest.substring(pos).trim();
			pos = tmp.indexOf(" ");
			defaultValue = tmp;
			if (pos > -1) {
				defaultValue = tmp.substring(0, pos);
			}
			columnDataRest = columnDataRest.replace(DEFAULT_IDENTIFIER + defaultValue, "")
					               .replace("  ", " ");
		}
		columnDataRest = columnDataRest.trim();

		int pos = columnDataRest.indexOf(" ");
		String columnName = columnDataRest.substring(0, pos);
		String columnType = columnDataRest.substring(pos+1);
		
		if (columnType.contains(" ")) {
			throw new RuntimeException("Invalid column definition: " + columnData);
		}
		
		ColumnMetaData column = new ColumnMetaData(new Name(columnName), 
				                                   new Type(columnType),
				                                   isColumnNullable);
		column.setDefault(defaultValue);
		column.setGenerationInfo(generationInfo);
		tableMetaData.addColumn(column);
		return columnDataRest;
	}

}