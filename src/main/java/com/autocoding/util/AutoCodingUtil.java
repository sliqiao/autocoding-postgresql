package com.autocoding.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.autocoding.codebuilder.BaseCodeBuilder;
import com.autocoding.constant.ConfigConstant;
import com.autocoding.model.Entity;
import com.autocoding.model.Project;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @ClassName: AutoCodingUtil
 * @Description: (这里用一句话描述这个类的作用)
 * @author: QiaoLi
 * @date: Jul 15, 2020 2:13:06 PM
 */
@Slf4j
public class AutoCodingUtil {

	private Connection conn;
	private String jdbcDriver;
	private String jdbcUrl;
	private String userName;
	private String password;
	private String rootPackage;
	private Project project;
	private String tablesName;

	public static AutoCodingUtil newInstance(String outputDir, Map<String, String> propertyMap) {
		AutoCodingUtil autoCodingMain = new AutoCodingUtil(outputDir, propertyMap);
		return autoCodingMain;

	}

	public void startAutoCoding() {
		this.connect();
		this.run();
		this.disconnect();

	}

	public AutoCodingUtil(String outputDir, Map<String, String> propertyMap) {
		try {
			String rootdir = System.getProperty("user.dir");
			this.jdbcDriver = propertyMap.get(ConfigConstant.C3P0_DRIVERCLASS);
			this.jdbcUrl = propertyMap.get(ConfigConstant.C3P0_JDBCURL);
			this.userName = propertyMap.get(ConfigConstant.C3P0_USER);
			this.password = propertyMap.get(ConfigConstant.C3P0_PASSWORD);
			this.rootPackage = propertyMap.get(ConfigConstant.AUTOCODING_PACKAGE);
			this.tablesName = propertyMap.get(ConfigConstant.AUTOCODING_TABLE_NAMES);
			String prefix = propertyMap.get(ConfigConstant.AUTOCODING_PREFIX);
			this.project = new Project(rootdir, this.rootPackage, this.userName, outputDir);
			this.project.setPrefix(prefix);
			this.project.setAuthor(propertyMap.get(ConfigConstant.AUTOCODING_AUTHOR));
			String prefixWithSharp = prefix.replace(".", "/");
			this.project.setPrefixWithSharp(prefixWithSharp);
		} catch (Exception e) {
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
		String[] names = this.tablesName.split(",");
		for (String name : names) {
			log.info("---------开始生成表[" + name + "]的代码--------------");
			this.run(name);
			log.info("---------完成生成表[" + name + "]的代码--------------");
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
				log
						.info("正在对【" + tableName + "】生成文件【" + codeBuilder.getFileoutputPath() + "】 ---【开始】");
				codeBuilder.saveToFile();
				log
						.info("正在对【" + tableName + "】生成文件【" + codeBuilder.getFileoutputPath() + "】 ---【结束】");
			}
		} catch (Exception e) {
			log.error("执行AutoCodingMain.run()异常：", e);

		}
	}

	public Project getProject() {
		return this.project;
	}

	public static void main(String[] args) {
		Map<String, String> propertyMap = new HashMap<String, String>(10);
		propertyMap.put(ConfigConstant.C3P0_DRIVERCLASS, "oracle.jdbc.driver.OracleDriver");
		propertyMap.put(ConfigConstant.C3P0_JDBCURL, "jdbc:oracle:thin:@192.168.222.223:1521:ORCL");
		propertyMap.put(ConfigConstant.C3P0_USER, "hrp_easyui");
		propertyMap.put(ConfigConstant.C3P0_PASSWORD, "hrp_easyui");
		propertyMap.put(ConfigConstant.AUTOCODING_PACKAGE, "com.th.supcom.hrp.rmps");
		propertyMap.put(ConfigConstant.AUTOCODING_TABLE_NAMES, "APP_DEVELOPER_INFO");
		propertyMap.put(ConfigConstant.AUTOCODING_PREFIX, "test");
		propertyMap.put(ConfigConstant.AUTOCODING_AUTHOR, "李桥");
		String outputDir = "E:\\xml_message\\" + UUID.randomUUID();

		AutoCodingUtil autoCodingUtil = AutoCodingUtil.newInstance(outputDir, propertyMap);
		autoCodingUtil.startAutoCoding();

		FileCompressUtil.compress(autoCodingUtil.project.getOutputPath(), "E:\\xml_message\\output.zip");
		try {
			org.apache.commons.io.FileUtils.deleteDirectory(new File(autoCodingUtil.project.getOutputPath()));
		} catch (IOException e) {
			log.error("IO异常",e);
		}

	}
}
