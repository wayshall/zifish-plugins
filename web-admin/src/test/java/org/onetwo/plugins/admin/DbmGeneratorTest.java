package org.onetwo.plugins.admin;

import org.junit.Test;
import org.onetwo.common.spring.test.SpringBaseJUnitTestCase;
import org.onetwo.dbm.spring.EnableDbm;
import org.onetwo.dbm.test.DbmGeneratorBaseTestConfiguration;
import org.onetwo.dbm.ui.EnableDbmUI;
import org.onetwo.dbm.ui.generator.DUIGenerator;
import org.onetwo.plugins.admin.DbmGeneratorTest.DbmGeneratorTestConfiguration;
import org.onetwo.plugins.admin.controller.WebAdminBaseController;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * @author wayshall
 * <br/>
 */
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes=DbmGeneratorTestConfiguration.class)
public class DbmGeneratorTest extends SpringBaseJUnitTestCase {

	@Configuration
	@EnableDbm
	@EnableDbmUI
	public static class DbmGeneratorTestConfiguration extends DbmGeneratorBaseTestConfiguration {
		
		public DbmGeneratorTestConfiguration() {
			dburl = "jdbc:mysql://gz-cdb-ll3xmnv3.sql.tencentcdb.com:61575/party?&useSSL=false&characterEncoding=UTF-8";
			dbusername = "partydb";
			dbpassword = "db@party";
		}
		
	}
	
	
	@Test
	public void generateEntity(){
		DUIGenerator.dataSource(dataSource)
					.javaBasePackage(WebAdminPlugin.class.getPackage().getName())//基础包名
					.stripTablePrefix("")//生成的文件会去掉act_前缀
//					.pluginProjectDir(PluginMeta.by(TenantMgrPlugin.class).getName())//插件名称
					.pluginBaseController(WebAdminBaseController.class)
//					.pageFileBaseDir(vueDir)
						.webadminGenerator("admin_audit")
							.generateUIEntity()
							.generateServiceImpl()
						.end()
					.build()
					.generate();//生成文件
	}
	

}
