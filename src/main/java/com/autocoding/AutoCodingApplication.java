package com.autocoding;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.Set;

import javax.management.RuntimeErrorException;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.autocoding.codebuilder.BaseCodeBuilder;
import com.autocoding.constant.ConfigConstant;
import com.autocoding.model.Entity;
import com.autocoding.model.Project;
import com.autocoding.util.CodeBuilderFactory;
import com.autocoding.util.CodeBuilderScanUtil;
import com.autododing.ContextedExceptionTest.ContextedExceptionTest;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @ClassName: AutoCodingMain
 * @Description: (这里用一句话描述这个类的作用)
 * @author: QiaoLi
 * @date: Jul 15, 2020 2:14:16 PM
 */
@Slf4j
public class AutoCodingApplication {

	private static final String DEFAULT_ENCODING = "utf-8";
	private Connection conn;
	private Properties props;
	private String jdbcDriver;
	private String jdbcUrl;
	private String userName;
	private String password;
	private String rootPackage;
	private Project project;
	private String tablesName;

	public AutoCodingApplication() {
		try {
			String rootdir = System.getProperty("user.dir");
			this.props = new Properties();
			this.props.load(new InputStreamReader(new FileInputStream(rootdir + File.separator + "config.properties"),
					AutoCodingApplication.DEFAULT_ENCODING));
			this.jdbcDriver = this.props.getProperty(ConfigConstant.C3P0_DRIVERCLASS);
			this.jdbcUrl = this.props.getProperty(ConfigConstant.C3P0_JDBCURL);
			this.userName = this.props.getProperty(ConfigConstant.C3P0_USER);
			this.password = this.props.getProperty(ConfigConstant.C3P0_PASSWORD);
			this.rootPackage = this.props.getProperty(ConfigConstant.AUTOCODING_PACKAGE);
			this.tablesName = this.props.getProperty(ConfigConstant.AUTOCODING_TABLE_NAMES);
			String prefix = this.props.getProperty(ConfigConstant.AUTOCODING_PREFIX);
			this.project = new Project(rootdir, this.rootPackage, this.userName);
			this.project.setPrefix(prefix);
			this.project.setAuthor(this.props.getProperty(ConfigConstant.AUTOCODING_AUTHOR));
			String prefixWithSharp = prefix.replace(".", "/");
			this.project.setPrefixWithSharp(prefixWithSharp);
		} catch (Exception e) {
			e = new ContextedRuntimeException(e).addContextValue("user.dir", tablesName);
			log.error("执行AutoCodingMain.AutoCodingMain()异常:", e);
		}
	}

	private boolean connect() {
		try {
			Class.forName(this.jdbcDriver);

			this.conn = DriverManager.getConnection(this.jdbcUrl, this.userName, this.password);

			log.info("---------数据库连接成功--------------");
			return true;
		} catch (Exception e) {
			log.error("执行AutoCodingMain.connect()异常:", e);
		}
		log.info("----------数据库连接失败--------------");

		return false;
	}

	private boolean disconnect() {
		try {
			this.conn.close();
			log.info("----------数据库断开成功--------------");
			return true;
		} catch (Exception e) {
			log.error("----------数据库断开失败--------------", e);
		}
		return false;
	}

	public void run() {
		String[] tableNameArray = this.tablesName.split(",");
		for (String tableName : tableNameArray) {
			log.info("---------开始生成表[" + tableName + "]的代码--------------");
			this.run(tableName);
			log.info("---------完成生成表[" + tableName + "]的代码--------------");
		}
	}

	public void run(String tableName) {
		try {
			Entity entity = new Entity(this.conn, tableName);
			entity.parseEntityInfo();
			this.project.setEntity(entity);

			BaseCodeBuilder codeBuilder = null;
			Set<Class<?>> codeBuilderSet = CodeBuilderScanUtil.scan();
			for (Class<?> codeBuilderClass : codeBuilderSet) {
				codeBuilder = CodeBuilderFactory.createBuilder(codeBuilderClass, this.project);
				log.info("正在对【" + tableName + "】生成文件【" + codeBuilder.getFileoutputPath() + "】 ---【开始】");
				codeBuilder.createEmptyDir();
				codeBuilder.saveToFile();
				log.info("正在对【" + tableName + "】生成文件【" + codeBuilder.getFileoutputPath() + "】 ---【结束】");
			}
		} catch (Exception e) {
			log.error("执行AutoCodingApplication.run()异常：", e);

		}
	}

	public static void main(String[] args) {
		AutoCodingApplication application = new AutoCodingApplication();
		application.connect();
		application.run();
		application.disconnect();
	}

}
