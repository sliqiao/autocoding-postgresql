package com.autocoding.enums;

import java.util.Objects;

/**
 * 
 * @ClassName: DataBaseTypeEnum
 * @Description:数据库类型枚举
 * @author: QiaoLi
 * @date: Jul 15, 2020 2:31:31 PM
 */
public enum TestEnum implements BaseEnum<Integer, TestEnum> {
	/** mysql */
	MYSQL(1, "MYSQL"),
	/** postgresql */
	POSTGRE_SQL(2, "POSTGRE_SQL");
	private Integer code;
	private String name;

	@Override
	public Integer getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	private TestEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	@Override
	public TestEnum parseByCode(Integer code) {
		if (null == code || code.equals("")) {
			return null;
		}
		for (TestEnum baseEnum : TestEnum.values()) {
			if (Objects.equals(code, baseEnum.getCode())) {
				return baseEnum;
			}
		}
		return null;
	}
 
}
