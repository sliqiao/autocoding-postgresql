package com.autocoding.enums;

import org.apache.commons.lang3.EnumUtils;

public class BaseEnumUtil {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <EnumType, CodeType> EnumType parseByCode(Class<EnumType > enumTypeClass, CodeType code) {
		BaseEnum baseEnum = BaseEnumUtil.getInstance(enumTypeClass);
		return (EnumType) baseEnum.parseByCode(code);

	}

	@SuppressWarnings("rawtypes")
	private static BaseEnum getInstance(Class clazz) {
		@SuppressWarnings("unchecked")
		BaseEnum baseEnum = (BaseEnum) EnumUtils.getEnumList(clazz).get(0);
		return baseEnum;

	}

	public static void main(String[] args) {
		DataBaseTypeEnum baseEnum = BaseEnumUtil.parseByCode(DataBaseTypeEnum.class, "mysql");
		TestEnum testEnum = BaseEnumUtil.parseByCode(TestEnum.class, 2);
		System.err.println("baseEnum:" + baseEnum);
		System.err.println("testEnum:" + testEnum);
	}
}
