package com.autocoding.codebuilder;

import java.util.Map;

import com.autocoding.container.CodeBuilderContainer;
import com.autocoding.freemarker.FreemarkerUtil;
import com.autocoding.model.CodeBuilderAnnotation;
import com.autocoding.model.Project;

/**
 * 生成MybatisPlus Mapper接口
 * 
 * @ClassName: MybatisPlusMapperBuilder
 * @author: QiaoLi
 * @date: Oct 14, 2020 3:01:11 PM
 */
@CodeBuilderAnnotation(desc = "生成MybatisPlus Mapper接口")
public class MybatisPlusMapperBuilder extends BaseCodeBuilder {

	public MybatisPlusMapperBuilder(Project project) {
		super(project);
		super.fileoutputPath = this.project.getMapperPath() + this.entity.getEntityName() + "Mapper.java";

	}

	@Override
	public void saveToFile() {
		Map<String, Object> rootMap = super.rootMap;
		FreemarkerUtil.fileExport(rootMap, this.project.getTemplatePath(),
				CodeBuilderContainer.getTempleateFileName(this.getClass()), super.fileoutputPath);

	}

}
