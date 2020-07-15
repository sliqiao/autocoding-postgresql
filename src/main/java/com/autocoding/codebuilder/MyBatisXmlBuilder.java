package com.autocoding.codebuilder;

import java.util.Map;

import com.autocoding.container.CodeBuilderContainer;
import com.autocoding.freemarker.FreemarkerUtil;
import com.autocoding.model.CodeBuilderAnnotation;
import com.autocoding.model.Project;

/**
 * 
 * @ClassName: MyBatisXmlBuilder
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: QiaoLi
 * @date: Jul 15, 2020 2:13:41 PM
 */
@CodeBuilderAnnotation(desc = "生成Mybatis的映射文件-sqlmap")
public class MyBatisXmlBuilder extends BaseCodeBuilder {

	public MyBatisXmlBuilder(Project project) {
		super(project);
		super.fileoutputPath = super.project.getDbconfigPath() + super.project.getEntity().getEntityName()
				+ ".sqlmap.xml";
	}

	@Override
	public void saveToFile() {
		Map<String, Object> rootMap = super.rootMap;
		FreemarkerUtil.fileExport(rootMap, this.project.getTemplatePath(),
				CodeBuilderContainer.getTempleateFileName(this.getClass()), super.fileoutputPath);

	}
}
