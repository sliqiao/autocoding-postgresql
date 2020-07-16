package com.autocoding.container;


import com.autocoding.freemarker.FreemarkerTag;

import lombok.extern.slf4j.Slf4j;

/**
 * 代码构建器容器
 * 
 * @date 2018年1月6日 下午2:38:42
 * @author 李桥
 * @version 1.0
 */
@Slf4j
public class CodeBuilderContainer {

	public static String getTempleateFileName(Class<?> codeBuilderClass) {
		if (null == codeBuilderClass) {
			log.error("codeBuilderClass不能为空!");
		}
		return codeBuilderClass.getSimpleName() + FreemarkerTag.POSTFIX;

	}
}
