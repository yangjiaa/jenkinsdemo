package com.yj.jenkinsdemo.util;

import org.apache.commons.lang.ObjectUtils;
import org.apache.velocity.VelocityContext;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 代码生成类
 */
public class MybatisGeneratorUtil {

	// generatorConfig模板路径
	private static String generatorConfig_vm = "/templates/generatorConfig.vm";
	// Service模板路径
	private static String repository_vm = "/templates/Repository.vm";
	// Service模板路径
	private static String service_vm = "/templates/Service.vm";
	// ServiceMock模板路径
	private static String serviceMock_vm = "/templates/ServiceMock.vm";
	// ServiceImpl模板路径
	private static String serviceImpl_vm = "/templates/ServiceImpl.vm";

	/**
	 * 根据模板生成generatorConfig.xml文件
	 * @param jdbcDriver   驱动路径
	 * @param jdbcUrl      链接
	 * @param jdbcUsername 帐号
	 * @param jdbcPassword 密码
	 * @param module        项目模块
	 * @param database      数据库
	 * @param tablePrefix  表前缀
	 * @param packageName  包名
	 */
	public static void generator(
			String jdbcDriver,
			String jdbcUrl,
			String jdbcUsername,
			String jdbcPassword,
			String module,
			String database,
			String tablePrefix,
			String packageName,
			Map<String, String> lastInsertIdTables) throws Exception{

		String os = System.getProperty("os.name");
		String targetProject = "";
		String basePath = MybatisGeneratorUtil.class.getResource("/").toURI().getPath().replace("/target/classes/", "").replace(targetProject, "");
		if (os.toLowerCase().startsWith("win")) {
			generatorConfig_vm = MybatisGeneratorUtil.class.getResource(generatorConfig_vm).toURI().getPath().replaceFirst("/", "");
			service_vm = MybatisGeneratorUtil.class.getResource(service_vm).toURI().getPath().replaceFirst("/", "");
			repository_vm = MybatisGeneratorUtil.class.getResource(repository_vm).toURI().getPath().replaceFirst("/", "");
			serviceMock_vm = MybatisGeneratorUtil.class.getResource(serviceMock_vm).toURI().getPath().replaceFirst("/", "");
			serviceImpl_vm = MybatisGeneratorUtil.class.getResource(serviceImpl_vm).toURI().getPath().replaceFirst("/", "");
			basePath = basePath.replaceFirst("/", "");
		} else {
			generatorConfig_vm = MybatisGeneratorUtil.class.getResource(generatorConfig_vm).getPath();
			service_vm = MybatisGeneratorUtil.class.getResource(service_vm).getPath();
			repository_vm = MybatisGeneratorUtil.class.getResource(repository_vm).getPath();
			serviceMock_vm = MybatisGeneratorUtil.class.getResource(serviceMock_vm).getPath();
			serviceImpl_vm = MybatisGeneratorUtil.class.getResource(serviceImpl_vm).getPath();
		}

		String generatorConfigXml = MybatisGeneratorUtil.class.getResource("/").toURI().getPath().replace("/target/classes/", "") + "/src/main/resources/generatorConfig.xml";
		targetProject = basePath + targetProject;
		String sql = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = '" + database + "' AND table_name LIKE '" + tablePrefix + "%';";
//		sql = "show tables";
		System.out.println("========== 开始生成generatorConfig.xml文件 ==========");
		List<Map<String, Object>> tables = new ArrayList<>();
		try {
			VelocityContext context = new VelocityContext();
			Map<String, Object> table;

			// 查询定制前缀项目的所有表
			JdbcUtil jdbcUtil = new JdbcUtil(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword);
			List<Map> result = jdbcUtil.selectByParams(sql, null);
			for (Map map : result) {
				System.out.println(map.get("TABLE_NAME"));
				table = new HashMap<>(2);
				table.put("table_name", map.get("TABLE_NAME"));
				table.put("model_name", StringUtil.lineToHump(ObjectUtils.toString(map.get("TABLE_NAME"))).substring(1));
				tables.add(table);
			}
			jdbcUtil.release();

//			String targetProjectSqlMap = basePath + module + "/" + module + "-rpc-service"+ module + "/" + module + "-rpc-service";
			String targetProjectSqlMap = basePath;
			context.put("tables", tables);
			context.put("generator_javaModelGenerator_targetPackage", packageName + ".entity");
			context.put("generator_sqlMapGenerator_targetPackage", packageName + ".mapper");
			context.put("generator_javaClientGenerator_targetPackage", packageName + ".mapper");
			context.put("targetProject", targetProject);
			context.put("targetProject_sqlMap", targetProjectSqlMap);
			context.put("generator_jdbc_password", jdbcPassword);
			context.put("last_insert_id_tables", lastInsertIdTables);
			VelocityUtil.generate(generatorConfig_vm, generatorConfigXml, context);
			// 删除旧代码
			deleteDir(new File(targetProject + "/src/main/java/" + packageName.replaceAll("\\.", "/") + "/dao/model"));
			deleteDir(new File(targetProject + "/src/main/java/" + packageName.replaceAll("\\.", "/") + "/dao/mapper"));
			deleteDir(new File(targetProjectSqlMap + "/src/main/java/" + packageName.replaceAll("\\.", "/") + "/dao/mapper"));
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}
		System.out.println("========== 结束生成generatorConfig.xml文件 ==========");

		System.out.println("========s== 开始运行MybatisGenerator ==========");
		List<String> warnings = new ArrayList<>();
		File configFile = new File(generatorConfigXml);
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(configFile);
		DefaultShellCallback callback = new DefaultShellCallback(true);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate(null);
		for (String warning : warnings) {
			System.out.println(warning);
		}
		System.out.println("========== 结束运行MybatisGenerator ==========");
//		if(true){
//		    return ;
//        }


		System.out.println("========== 开始生成Service ==========");
		String ctime = new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
		String repositoryPath = basePath + "/src/main/java/" + packageName.replaceAll("\\.", "/") + "/repository";
		String servicePath = basePath + "/src/main/java/" + packageName.replaceAll("\\.", "/") + "/service";
		String serviceImplPath = basePath + "/src/main/java/" + packageName.replaceAll("\\.", "/") + "/service/impl";
		for (int i = 0; i < tables.size(); i++) {

			String model = StringUtil.lineToHump(ObjectUtils.toString(tables.get(i).get("table_name"))).substring(1);
			String repository = repositoryPath + "/" + model + "Repository.java";
			String service = servicePath + "/" + model + "Service.java";
//			String serviceMock = servicePath + "/" + model + "ServiceMock.java";
			String serviceImpl = serviceImplPath + "/" + model + "ServiceImpl.java";
			// 生成repository
			File repositoryFile = new File(repository);
			if (!repositoryFile.exists()) {
				repositoryFile.getParentFile().mkdirs();
				VelocityContext context = new VelocityContext();
				context.put("package_name", packageName);
				context.put("model", model);
				context.put("ctime", ctime);
				VelocityUtil.generate(repository_vm, repository, context);
				System.out.println(service);
			}
			// 生成service
			File serviceFile = new File(service);
			if (!serviceFile.exists()) {
				serviceFile.getParentFile().mkdirs();
				VelocityContext context = new VelocityContext();
				context.put("package_name", packageName);
				context.put("model", model);
				context.put("ctime", ctime);
				VelocityUtil.generate(service_vm, service, context);
				System.out.println(service);
			}
			// 生成serviceMock
			/*File serviceMockFile = new File(serviceMock);
			if (!serviceMockFile.exists()) {
				serviceMockFile.getParentFile().mkdirs();
				VelocityContext context = new VelocityContext();
				context.put("package_name", packageName);
				context.put("model", model);
				context.put("ctime", ctime);
				VelocityUtil.generate(serviceMock_vm, serviceMock, context);
				System.out.println(serviceMock);
			}*/
			// 生成serviceImpl
			File serviceImplFile = new File(serviceImpl);
			if (!serviceImplFile.exists()) {
				serviceImplFile.getParentFile().mkdirs();
				VelocityContext context = new VelocityContext();
				context.put("package_name", packageName);
				context.put("model", model);
				context.put("mapper", StringUtil.toLowerCaseFirstOne(model));
				context.put("ctime", ctime);
				VelocityUtil.generate(serviceImpl_vm, serviceImpl, context);
				System.out.println(serviceImpl);
			}
		}
		System.out.println("========== 结束生成Service ==========");
	}

	// 递归删除非空文件夹
	public static void deleteDir(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteDir(files[i]);
			}
		}
		dir.delete();
	}

}
