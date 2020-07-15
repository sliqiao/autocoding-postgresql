package com.autocoding.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.autocoding.enums.DataBaseTypeEnum;
import com.autocoding.freemarker.FreemarkerTag;
import com.autocoding.util.ColumnUtil;

/**
 * @author Administrator
 * 
 */
public class Entity {
	private final Connection connection;
	private final String tableName;
	private String tableComments;
	private final String entityName;
	private String primaryKeyFieldName;
	private String primaryKeyPropertyName;
	private Field primaryKeyField;
	private final ArrayList<Field> fields;
	private String entityBeanName;
	private String toStr;
	private final String selectSqlName;
	private final String insertSqlName;
	private final String updateSqlName;
	private final String deleteSqlName;
	private final String selectAllSqlName;

	public Entity(Connection connection, String tableName) {
		super();
		this.entityName = ColumnUtil.getJavaBeanNameBy(tableName);
		this.connection = connection;
		this.tableName = tableName;
		this.fields = new ArrayList<Field>();
		this.entityBeanName = this.entityName.substring(0, 1).toLowerCase();
		this.entityBeanName += this.entityName.substring(1);
		this.selectAllSqlName = "select";
		this.selectSqlName = "view";
		this.insertSqlName = "insert";
		this.updateSqlName = "update";
		this.deleteSqlName = "delete";
	}

	public void parseEntityInfo() throws SQLException {
		String fieldName;
		String fieldDes;
		boolean nullable;
		int iColumnType;
		int precision;
		int scale;
		String propertyName;
		DatabaseMetaData databaseMetaData;
		Statement stmt = this.connection.createStatement();
		String databaseTypeCode = this.connection.getMetaData().getDatabaseProductName();
		String columnCommentsSql = "";
		String tableCommentsSql = "";
		if (databaseTypeCode.toLowerCase().indexOf(DataBaseTypeEnum.MYSQL.getCode()) != -1) {
			columnCommentsSql = " SELECT B.COLUMN_NAME,B.COLUMN_COMMENT COMMENTS FROM INFORMATION_SCHEMA.COLUMNS B WHERE  B.TABLE_NAME='"
					+ this.tableName + "'";
		} else if (databaseTypeCode.toLowerCase().indexOf(DataBaseTypeEnum.POSTGRE_SQL.getCode()) != -1) {
			columnCommentsSql = " SELECT a.attname AS \"COLUMN_NAME\",b.description AS \"COMMENTS\" FROM pg_class c,pg_attribute a"
					+ " LEFT OUTER JOIN pg_description b ON a.attrelid=b.objoid AND a.attnum = b.objsubid,pg_type t "
					+ "WHERE c.relname = '" + this.tableName
					+ "' and a.attnum > 0 and a.attrelid = c.oid and a.atttypid = t.oid";
		} else if (databaseTypeCode.toLowerCase().indexOf(DataBaseTypeEnum.MICROSOFT_SQL_SERVER.getCode()) != -1) {
			return;
		} else if (databaseTypeCode.toLowerCase().indexOf(DataBaseTypeEnum.ORACLE.getCode()) != -1) {
			columnCommentsSql = " SELECT B.COLUMN_NAME,B.COMMENTS FROM USER_COL_COMMENTS B WHERE  B.TABLE_NAME='"
					+ this.tableName + "'";
			tableCommentsSql = " SELECT B.TABLE_NAME,B.COMMENTS FROM USER_TAB_COMMENTS B WHERE  B.TABLE_NAME='"
					+ this.tableName + "'";
			// 获取表注释
			ResultSet tableCommentsResultSet = stmt.executeQuery(tableCommentsSql);
			while (tableCommentsResultSet.next()) {
				this.tableComments = tableCommentsResultSet.getString("COMMENTS");
			}
		} else if (databaseTypeCode.toLowerCase().indexOf(DataBaseTypeEnum.DB2.getCode()) != -1) {
			System.out.println("没有db2查询数据库表结构SQL");
			return;
		}

		// 获取字段注释名称
		Map<String, String> columnComments = new HashMap<String, String>(50);
		ResultSet columnCommentReslutSet = stmt.executeQuery(columnCommentsSql);
		while (columnCommentReslutSet.next()) {
			columnComments.put(columnCommentReslutSet.getString("COLUMN_NAME"), columnCommentReslutSet.getString("COMMENTS"));
		}
		columnCommentReslutSet.close();
		ResultSet columnResultSet = stmt.executeQuery("SELECT * FROM " + this.tableName + " WHERE 1 = 2 ");
		ResultSetMetaData metaData = columnResultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		databaseMetaData = this.connection.getMetaData();
		ResultSet priKeySet = databaseMetaData.getPrimaryKeys(null, null, this.tableName);
		if (priKeySet.next()) {
			this.primaryKeyFieldName = priKeySet.getString(4);
			this.primaryKeyPropertyName = ColumnUtil.getJavaBeanPropsNameBy(this.primaryKeyFieldName);
			this.primaryKeyField = new Field(this, this.primaryKeyFieldName, this.primaryKeyPropertyName, true,
					columnComments.get(this.primaryKeyFieldName));
		}
		priKeySet.close();

		for (int i = 1; i <= columnCount; i++) {
			fieldName = metaData.getColumnName(i);
			iColumnType = metaData.getColumnType(i);
			precision = metaData.getPrecision(i);
			scale = metaData.getScale(i);
			if (metaData.isNullable(i) == 1) {
				nullable = true;
			} else {
				nullable = false;
			}
			fieldDes = columnComments.get(fieldName);
			if (!fieldName.equalsIgnoreCase(this.primaryKeyFieldName)) {
				propertyName = ColumnUtil.getJavaBeanPropsNameBy(fieldName);
				Field field = new Field(this, fieldName, propertyName, false, fieldDes);
				field.setNullable(nullable);
				field.setDataType(iColumnType);
				field.setColumnLength(metaData.getColumnDisplaySize(i));
				field.setPrecision(precision);
				field.setScale(scale);
				this.fields.add(field);
			} else {
				if (null != this.primaryKeyField) {
					this.primaryKeyField.setDataType(iColumnType);
					this.primaryKeyField.setPrecision(precision);
					this.primaryKeyField.setScale(scale);
					this.primaryKeyField.setColumnLength(metaData.getColumnDisplaySize(i));
				}
			}
		}
		if (null == this.primaryKeyField) {
			this.primaryKeyField = this.fields.get(0);
		}
		columnResultSet.close();
		stmt.close();
	}

	public String getToStr() {
		StringBuffer codeBuffer = new StringBuffer();
		codeBuffer.append("    @Override\r\n");
		codeBuffer.append("    public String toString() {\r\n");
		codeBuffer.append("       StringBuilder builder = new StringBuilder();\r\n");
		codeBuffer.append("		builder.append(\"" + this.getEntityName() + "[\");\r\n");
		Field pkfield = this.getPrimaryKeyField();
		codeBuffer.append("		builder.append(  \" " + pkfield.getFieldDes() + "[" + pkfield.getPropertyName()
				+ "] = \");\r\n");
		codeBuffer.append("		builder.append(this." + FreemarkerTag.Common.FIELD_PREFIX + pkfield.getPropertyName()
				+ " );\r\n");
		int iFieldCount = this.getFields().size();
		for (int i = 0; i < iFieldCount; i++) {
			Field field = this.getFields().get(i);
			int endIndex = field.getFieldDes().length() - 1;
			if (field.getFieldDes().length() > Field.MAX_FIELD_DESC_LENGTH) {
				endIndex = Field.MAX_FIELD_DESC_LENGTH;
			}
			codeBuffer.append("		builder.append(  \" " + field.getFieldDes().substring(0, endIndex + 1) + "["
					+ field.getPropertyName() + "] = \");\r\n");
			codeBuffer.append("		builder.append(this." + FreemarkerTag.Common.FIELD_PREFIX + field.getPropertyName()
					+ " );\r\n");
		}
		codeBuffer.append("		builder.append(\"]\");\r\n");
		codeBuffer.append("		return builder.toString();\r\n");
		codeBuffer.append("	}\r\n");
		return codeBuffer.toString();

	}

	public Field getPrimaryKeyField() {
		return this.primaryKeyField;
	}

	public String getEntityName() {
		return this.entityName;
	}

	public String getTableName() {
		return this.tableName;
	}

	public ArrayList<Field> getFields() {
		return this.fields;
	}

	public String getEntityBeanName() {
		return this.entityBeanName;
	}

	public String getSelectSqlName() {
		return this.selectSqlName;
	}

	public String getSelectAllSqlName() {
		return this.selectAllSqlName;
	}

	public String getInsertSqlName() {
		return this.insertSqlName;
	}

	public String getUpdateSqlName() {
		return this.updateSqlName;
	}

	public String getDeleteSqlName() {
		return this.deleteSqlName;
	}

	public String getTableComments() {
		if (null == this.tableComments) {
			return "";
		}
		return this.tableComments;
	}

	public void setTableComments(String tableComments) {
		this.tableComments = tableComments;
	}

}
