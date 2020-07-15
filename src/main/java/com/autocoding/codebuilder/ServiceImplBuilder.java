package com.autocoding.codebuilder;

import java.util.Map;

import com.autocoding.container.CodeBuilderContainer;
import com.autocoding.freemarker.FreemarkerUtil;
import com.autocoding.model.CodeBuilderAnnotation;
import com.autocoding.model.Project;

/**
 * 
 * @ClassName: ServiceImplBuilder
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: QiaoLi
 * @date: Jul 15, 2020 2:14:22 PM
 */
@CodeBuilderAnnotation(desc = "生成ServiceImpl文件")
public class ServiceImplBuilder extends BaseCodeBuilder {

	public ServiceImplBuilder(Project project) {
		super(project);
		super.fileoutputPath = super.project.getServiceImplPath() + super.entity.getEntityName() + "ServiceImpl.java";

	}

	@Override
	public void saveToFile() {
		Map<String, Object> rootMap = super.rootMap;
		FreemarkerUtil.fileExport(rootMap, this.project.getTemplatePath(),
				CodeBuilderContainer.getTempleateFileName(this.getClass()), super.fileoutputPath);

	}

}
