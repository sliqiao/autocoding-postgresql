package com.autocoding.codebuilder;

import java.util.Map;

import com.autocoding.container.CodeBuilderContainer;
import com.autocoding.freemarker.FreemarkerUtil;
import com.autocoding.model.CodeBuilderAnnotation;
import com.autocoding.model.Project;

/**
 * 
 * Entity代码生成器
 * 
 * @date 2018年1月6日 下午2:33:59
 * @author 李桥
 * @version 1.0
 */
@CodeBuilderAnnotation(desc = "生成Package注释")
public class PackageBuilder extends BaseCodeBuilder {
	public PackageBuilder(Project project) {
		super(project);
		super.fileoutputPath = super.project. getRootPackagePath() + "package-info.java";
	}

	@Override
	public void saveToFile() {
		Map<String, Object> rootMap = super.rootMap;
		FreemarkerUtil.fileExport(rootMap, this.project.getTemplatePath(),
				CodeBuilderContainer.getTempleateFileName(this.getClass()), super.fileoutputPath);

	}
}
