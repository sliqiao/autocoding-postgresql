package com.autocoding.util;

import java.lang.reflect.Constructor;


import com.autocoding.codebuilder.BaseCodeBuilder;
import com.autocoding.model.Project;

import lombok.extern.slf4j.Slf4j;

/**
 * CodeBuilder工厂
 * 
 * @date 2018年1月6日 下午2:13:46
 * @author 李桥
 * @version 1.0
 */
@Slf4j
public class CodeBuilderFactory {

	public static BaseCodeBuilder createBuilder(Class clazz, Project project) {
		BaseCodeBuilder absCodeBuilder = null;
		try {
			Constructor<?> constructor = clazz.getConstructor(Project.class);
			absCodeBuilder = (BaseCodeBuilder) constructor.newInstance(project);
		} catch (Exception e) {
			log.error("构建对象异常：", clazz.getName());
		}
		return absCodeBuilder;
	}
}
