package com.autocoding.codebuilder;

import java.util.Map;

import com.autocoding.container.CodeBuilderContainer;
import com.autocoding.freemarker.FreemarkerUtil;
import com.autocoding.model.CodeBuilderAnnotation;
import com.autocoding.model.Project;

/**
 * 
 * @ClassName: IBatisBuilder
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: QiaoLi
 * @date: Jul 15, 2020 2:13:34 PM
 */
@CodeBuilderAnnotation(desc = "生成Ibatis配置文件")
public class IBatisBuilder extends BaseCodeBuilder {

	public IBatisBuilder(Project project) {
		super(project);
		this.fileoutputPath = this.project.getDbconfigPath() + this.project.getEntity().getEntityName() + "_ibatis.xml";
	}

	@Override
	public void saveToFile() {

		Map<String, Object> rootMap = super.rootMap;
		FreemarkerUtil.fileExport(rootMap, this.project.getTemplatePath(),
				CodeBuilderContainer.getTempleateFileName(this.getClass()), super.fileoutputPath);

	}
}
