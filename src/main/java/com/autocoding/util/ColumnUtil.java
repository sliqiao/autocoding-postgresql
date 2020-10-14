package com.autocoding.util;

import java.sql.Types;

/**
 * 
 * @ClassName: ColumnUtil
 * @Description: (这里用一句话描述这个类的作用)
 * @author: QiaoLi
 * @date: Jul 15, 2020 2:14:03 PM
 */
public class ColumnUtil {

	private ColumnUtil() {
	}

	public static String getJavaBeanNameBy(String entityName) {
		String javaBeanName = "";
		boolean bFlag = false;
		char ch;

		entityName = entityName.toUpperCase();
		for (int i = 0; i < entityName.length(); i++) {
			ch = entityName.charAt(i);
			if (!bFlag) {
				javaBeanName += ch;
				bFlag = true;
			} else {
				if (ch != '_') {
					javaBeanName += Character.toString(entityName.charAt(i)).toLowerCase();
				} else {
					bFlag = false;
				}
			}
		}

		return javaBeanName;
	}

	public static String getGetSetterNameByProp(String propName) {
		String getSetterName = propName.substring(0, 1).toUpperCase();
		getSetterName += propName.substring(1);

		return getSetterName;
	}

	public static String getJavaBeanPropsNameBy(String columnName) {
		String javaBeanName = "";
		boolean bFlag = false;
		boolean bFirstFlag = false;
		char ch;

		columnName = columnName.toUpperCase();
		for (int i = 0; i < columnName.length(); i++) {
			ch = columnName.charAt(i);
			if (!bFirstFlag) {
				javaBeanName += Character.toString(columnName.charAt(i)).toLowerCase();
				bFirstFlag = true;
				bFlag = true;
				continue;
			}

			if (!bFlag) {
				javaBeanName += ch;
				bFlag = true;
			} else {
				if (ch != '_') {
					javaBeanName += Character.toString(columnName.charAt(i)).toLowerCase();
				} else {
					bFlag = false;
				}
			}
		}

		return javaBeanName;
	}

	public static String getJavaType(int iDataType, int scale) {
		String javaType = "";
		if (iDataType == Types.VARCHAR || iDataType == Types.CHAR || iDataType == Types.LONGVARCHAR
				|| iDataType == Types.CLOB) {
			javaType = "String";
		} else if (iDataType == Types.INTEGER || iDataType == Types.BIT || iDataType == Types.TINYINT
				|| iDataType == Types.SMALLINT || iDataType == Types.NUMERIC) {
			javaType = "Integer";
			if (scale > 0) {
				javaType = "Double";
			}
		} else if (iDataType == Types.BIGINT) {
			javaType = "Long";
			if (scale > 0) {
				javaType = "Double";
			}
		} else if (iDataType == Types.DOUBLE || iDataType == Types.FLOAT || iDataType == Types.DECIMAL) {
			javaType = "Double";
		} else if (iDataType == Types.DATE || iDataType == Types.TIME) {
			javaType = "Date";
		} else if (iDataType == Types.TIMESTAMP) {
			javaType = "Timestamp";
		} else if (iDataType == Types.BLOB) {
			javaType = "byte[]";
		}

		return javaType;
	}

	public static String getJdbcType(int iDataType, int scale) {
		String jdbcType = "Unkonwn";

		if (iDataType == Types.VARCHAR || iDataType == Types.CHAR || iDataType == Types.LONGVARCHAR
				|| iDataType == Types.CLOB) {
			jdbcType = "VARCHAR";
		} else if (iDataType == Types.INTEGER || iDataType == Types.BIT || iDataType == Types.TINYINT
				|| iDataType == Types.NUMERIC) {
			jdbcType = "INTEGER";
			if (scale > 0) {
				jdbcType = "DOUBLE";
			}
		} else if (iDataType == Types.BIGINT) {
			jdbcType = "LONG";
			if (scale > 0) {
				jdbcType = "DOUBLE";
			}
		} else if (iDataType == Types.DOUBLE || iDataType == Types.FLOAT) {
			jdbcType = "DOUBLE";
		} else if (iDataType == Types.DATE || iDataType == Types.TIMESTAMP || iDataType == Types.TIME) {
			jdbcType = "DATE";
		} else if (iDataType == Types.BLOB) {
			jdbcType = "byte[]";
		}

		return jdbcType;
	}

}
