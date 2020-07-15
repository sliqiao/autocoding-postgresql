package com.autocoding.freemarker;

/**
 * Module: FreemakerTag
 * 
 * @author liqiao
 * @since JDK 1.6
 * @version 1.0
 * @description: TODO
 * @log:2016年5月6日 上午9:25:34 liqiao create
 */
public interface FreemarkerTag {
	public static final String POSTFIX = ".fml";

	public interface Common {
		public static final String AUTHOR = "author";
		public static final String PROJECT = "project";
		public static final String PK_FIELD = "pkField";
		public static final String FIELDS = "fields";
		public static final String PREFIX_WITH_SHARP = "prefixWithSharp";
		public static final String ENTITY_BEAN_NAME = "entityBeanName";
		public static final String ENTITY_NAME = "entityName";
		public static final String ENTITY = "entity";
		public static final String DATE = "date";
		public static final String PACKAGE = "package";
		public static final String JSP_PATH = "jspPath";
		public static final String DB_2_MAP = "db2Map";
		public static final String PKID = "pkid";
		public static final String MODEL = "model";
		public static final String PREFIX = "prefix";
		public static final String FIELD_PREFIX = "";

	}
}
