package org.onetwo.plugins.admin;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.onetwo.boot.plugin.core.JFishWebPlugin;
import org.onetwo.common.exception.BaseException;
import org.onetwo.common.log.JFishLoggerFactory;
import org.onetwo.dbm.spring.EnableDbmRepository;
import org.onetwo.ext.permission.api.annotation.FullyAuthenticated;
import org.onetwo.ext.permission.entity.PermisstionTreeModel;
import org.onetwo.ext.permission.parser.DefaultMenuInfoParser;
import org.onetwo.ext.permission.parser.MenuInfoParser;
import org.onetwo.ext.permission.service.MenuItemRepository;
import org.onetwo.ext.permission.service.impl.DefaultMenuItemRepository;
import org.onetwo.ext.security.provider.CaptchaAuthenticationProvider;
import org.onetwo.ext.security.utils.SecurityConfig;
import org.onetwo.plugins.admin.controller.CaptchaController;
import org.onetwo.plugins.admin.controller.KindeditorController;
import org.onetwo.plugins.admin.controller.LoginController;
import org.onetwo.plugins.admin.controller.WebAdminBaseController;
import org.onetwo.plugins.admin.entity.AdminPermission;
import org.onetwo.plugins.admin.entity.AdminUser;
import org.onetwo.plugins.admin.event.CreateOrUpdateAdminUserListenner;
import org.onetwo.plugins.admin.listener.LoginSuccessListener;
import org.onetwo.plugins.admin.service.DictionaryImportService;
import org.onetwo.plugins.admin.service.impl.AdminUserDetailServiceImpl;
import org.onetwo.plugins.admin.service.impl.PermissionManagerImpl;
import org.onetwo.plugins.admin.utils.AdminTenantContextVariable;
import org.onetwo.plugins.admin.utils.WebAdminPermissionConfig;
import org.onetwo.plugins.admin.utils.WebAdminPermissionConfig.AdminPermissionConfigListAdapetor;
import org.onetwo.plugins.admin.utils.WebAdminPermissionConfig.RootMenuClassListProvider;
import org.onetwo.plugins.admin.utils.WebAdminPermissionConfig.RootMenuClassProvider;
import org.onetwo.plugins.admin.utils.WebAdminProperties;
import org.onetwo.plugins.admin.utils.WebAdminProperties.CaptchaProps;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.google.common.collect.Sets;


//@Configuration
//@ConditionalOnProperty(name="jfish.plugins.webAdmin.enabled", havingValue="true", matchIfMissing=true)
//@DbmPackages("org.onetwo.plugins.admin.dao")
@EnableDbmRepository("org.onetwo.plugins.admin.dao")
@Order(value=Ordered.LOWEST_PRECEDENCE)
@JFishWebPlugin(WebAdminPlugin.class)
@EnableConfigurationProperties(WebAdminProperties.class)
@ComponentScan(basePackageClasses= {LoginSuccessListener.class})
public class WebAdminPluginContext implements InitializingBean {
	
//	final private Logger logger = LoggerFactory.getLogger(this.getClass());

//	@Autowired
//	private BootSiteConfig bootSiteConfig;

	/****
	 * trigger baseEntityManager init and load the sql file
	 */
//	@Autowired
//	private BaseEntityManager baseEntityManager;
//	@Autowired
//	private ApplicationContext applicationContext;
//	@Autowired
//	private WebAdminProperties webAdminProperties;
	
	public WebAdminPluginContext(){
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}
	
	
	/*@Bean
	public WebAdminPlugin webAdminPlugin(){
		return new WebAdminPlugin();
	}*/
	
	
//	@Bean
//	static public ApplicationListener<SpringsInitEvent> webAdminApplicationListener(){
//		return new WebAdminApplicationListener();
//	}
	
	@Bean
	public CreateOrUpdateAdminUserListenner createOrUpdateAdminUserListenner() {
		return new CreateOrUpdateAdminUserListenner();
	}
	
	@Bean
	@ConditionalOnProperty(value={"site.kindeditor.imageBasePath"}, matchIfMissing=false)
	public KindeditorController kindeditorController(){
		return new KindeditorController();
	}
	
	

	/*@Configuration
	protected static class JavaClassStylePermissionManager {
		
	}*/
	
	@Bean
//	@Autowired
//	@ConditionalOnBean(RootMenuClassProvider.class)
	public AdminPermissionConfigListAdapetor adminPermissionConfigListAdapetor(@Autowired(required = false) Map<String, RootMenuClassProvider> providerMap){
		AdminPermissionConfigListAdapetor list = new AdminPermissionConfigListAdapetor();
		if (providerMap==null) {
			return list;
		}
		Logger logger = JFishLoggerFactory.getCommonLogger();
		if(logger.isInfoEnabled()){
			providerMap.forEach((k, v)->{
				Object rootMenuClass = null;
				if(v instanceof RootMenuClassListProvider){
					rootMenuClass = ((RootMenuClassListProvider)v).rootMenuClassList();
				}else{
					rootMenuClass = v.rootMenuClass();
				}
				logger.info("loading RootMenuClassProvider: {} -> {}", k, rootMenuClass);
			});
		}
		
		Collection<RootMenuClassProvider> providers = providerMap.values();
		providers.forEach(provider->{
			Collection<Class<?>> rooMenuClassList = new HashSet<>();
			if(provider instanceof RootMenuClassListProvider){
				rooMenuClassList.addAll(((RootMenuClassListProvider)provider).rootMenuClassList());
			}else{
				rooMenuClassList.add(provider.rootMenuClass());
			}
			rooMenuClassList.forEach(rootMenuClass->{
				WebAdminPermissionConfig config = new WebAdminPermissionConfig();
//				config.setRootMenuClassProvider(provider);
				config.setRootMenuClass(rootMenuClass);
				list.add(config);
			});
		});
		
		WebAdminPermissionConfig config = new WebAdminPermissionConfig();
		config.setRootMenuClass(FullyAuthenticated.class);
		list.add(config);
		return list;
	}
	
	@Bean
	@Autowired
	public PermissionManagerImpl permissionManagerImpl(AdminPermissionConfigListAdapetor configs){
		PermissionManagerImpl manager = new PermissionManagerImpl();
		Set<Class<?>> menuClasses = Sets.newHashSetWithExpectedSize(configs.size());
		List<MenuInfoParser<AdminPermission>> parsers = configs.stream().map(cfg->{
			if(menuClasses.contains(cfg.getRootMenuClass())){
				throw new BaseException("duplicate config menu class : " + cfg.getRootMenuClass());
			}
			menuClasses.add(cfg.getRootMenuClass());
			return new DefaultMenuInfoParser<AdminPermission>(cfg);
		})
		.collect(Collectors.toList());
		manager.setParsers(parsers);
		return manager;
	}
	
	/*@Bean
	@ConditionalOnBean(DictionaryService.class)
	@ConditionalOnMissingBean(DictionaryImportController.class)
	public DictionaryImportController dictionaryImportController(){
		return new DictionaryImportController();
	}*/

	/****
	 * 如果是sso client端，则不需要启用
	 * @author way
	 *
	 */
	@ComponentScan(basePackageClasses={WebAdminBaseController.class, DictionaryImportService.class, WebAdminPermissionConfig.class})
	@Configuration
//	@Conditional(NotEnableOauth2SsoCondition.class)
	protected static class WebAdminManagerModule {
		
		public WebAdminManagerModule(){
		}
		
		@Bean
		@ConditionalOnMissingBean(UserDetailsService.class)
		public UserDetailsService userDetailsService(){
			return new AdminUserDetailServiceImpl<AdminUser>(AdminUser.class);
		}
		
		@Bean
		public AdminTenantContextVariable adminTenantContextVariable() {
			return new AdminTenantContextVariable();
		}
		
		@Bean
		@ConditionalOnMissingBean(name="loginController")
		public LoginController loginController(){
			return new LoginController();
		}
		
		@Bean
//		@ConditionalOnBean(AdminController.class)
		@ConditionalOnMissingBean(MenuItemRepository.class)
		public MenuItemRepository<PermisstionTreeModel> menuItemRepository(){
			DefaultMenuItemRepository menuItemRepository = new DefaultMenuItemRepository();
			return menuItemRepository;
		}
	}
	
	@Configuration
	@ConditionalOnProperty(name=CaptchaProps.ENABLED_KEY, matchIfMissing=true)
	protected static class CaptchaConfiguration {
		@Autowired
		WebAdminProperties webAdminProperties;
		@Autowired
		private SecurityConfig securityConfig;
		
		@Bean
		public CaptchaAuthenticationProvider captchaAuthenticationProvider(){
			CaptchaAuthenticationProvider provider = new CaptchaAuthenticationProvider();
			provider.setCaptchaParameterName(webAdminProperties.getCaptcha().getParameterName());
			provider.setCaptchaCookieName(webAdminProperties.getCaptcha().getCookieName());
			provider.setCaptchaChecker(webAdminProperties.getCaptchaChecker());
			provider.setCookiePath(securityConfig.getCookie().getPath());
			return provider;
		}
		
		@Bean
		public CaptchaController captchaController(){
			CaptchaController captcha = new CaptchaController();
			return captcha;
		}
	}

}
