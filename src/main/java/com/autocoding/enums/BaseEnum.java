package com.autocoding.enums;

public interface BaseEnum<CodeType, EnumType> {
	
	public EnumType parseByCode(CodeType code);

	public CodeType getCode();
	 

}
