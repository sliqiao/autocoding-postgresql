package com.autocoding.codebuilder;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.autocoding.freemarker.FreemarkerTag;
import com.autocoding.model.Entity;
import com.autocoding.model.Project;

/**
 * 
 * 抽象代码构建器
 * 
 * @date 2018年1月6日 下午2:32:38
 * @author 李桥
 * @version 1.0
 */
public abstract class BaseCodeBuilder {
	protected String fileoutputPath;
	protected Project project;
	protected Entity entity;
	protected Map<String, Object> rootMap;
   /**
    * 输出到文件
    * @Title: saveToFile   
    * @param:       
    * @return: void      
    * @throws
    */
	public abstract void saveToFile();

	public BaseCodeBuilder(Project project) {
		this.project = project;
		this.entity = this.project.getEntity();
		this.rootMap = this.getRootMap();
	}

	public String getFileoutputPath() {
		return this.fileoutputPath;
	}
	public void createEmptyDir() {
		File tempDir=null;
		//生成vo、dto、constant、enums
		List<String> dirList=Arrays.asList("misc","domain","model","vo","dto","constant","enums","aop","config","httpclient","util");
		for(String dirName:dirList){
			tempDir=new File(this.project.getRootPackagePath()+File.separator+dirName);
			if(!tempDir.exists()){
				tempDir.mkdirs();
			}
		}
		 
	}
	private Map<String, Object> getRootMap() {
		Map<String, Object> rootMap = new HashMap<String, Object>(10);
		rootMap.put(FreemarkerTag.Common.AUTHOR, this.project.getAuthor());
		rootMap.put(FreemarkerTag.Common.PROJECT, this.project);
		rootMap.put(FreemarkerTag.Common.DATE, this.project.getDateString());
		rootMap.put(FreemarkerTag.Common.ENTITY, this.entity);
		rootMap.put(FreemarkerTag.Common.PACKAGE, this.project.getRootPackage());
		rootMap.put(FreemarkerTag.Common.PK_FIELD, this.entity.getPrimaryKeyField());
		rootMap.put(FreemarkerTag.Common.FIELDS, this.entity.getFields());
		rootMap.put(FreemarkerTag.Common.ENTITY_BEAN_NAME, this.entity.getEntityBeanName());
		rootMap.put(FreemarkerTag.Common.PREFIX, this.project.getPrefix());
		rootMap.put(FreemarkerTag.Common.ENTITY_NAME, this.entity.getEntityName());
		return rootMap;
	}

}
