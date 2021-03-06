package com.autocoding.enums;

import java.util.Objects;

/**
 * 
 * @ClassName: DataBaseTypeEnum
 * @Description:数据库类型枚举
 * @author: QiaoLi
 * @date: Jul 15, 2020 2:31:31 PM
 */
public enum DataBaseTypeEnum implements BaseEnum<String, DataBaseTypeEnum> {
	/** mysql */
	MYSQL("mysql", "MYSQL"),
	/** postgresql */
	POSTGRE_SQL("postgresql", "POSTGRE_SQL"),
	/** microsoft sql server */
	MICROSOFT_SQL_SERVER("microsoft sql server", "MICROSOFT_SQL_SERVER"),
	/** oracle */
	ORACLE("oracle", "ORACLE"),
	/** db2 */
	DB2("db2", "DB2");
	private String code;
	private String name;

	@Override
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	private DataBaseTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	@Override
	public DataBaseTypeEnum parseByCode(String code) {
		if (null == code || code.equals("")) {
			return null;
		}
		for (DataBaseTypeEnum baseEnum : DataBaseTypeEnum.values()) {
			if (Objects.equals(code, baseEnum.getCode())) {
				return baseEnum;
			}
		}
		return null;
	}
 
}
