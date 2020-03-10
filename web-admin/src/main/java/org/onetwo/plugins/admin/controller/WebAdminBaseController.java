package org.onetwo.plugins.admin.controller;

import org.onetwo.boot.core.web.controller.DateInitBinder;
import org.onetwo.boot.plugin.core.WebPlugin;
import org.onetwo.boot.plugin.mvc.PluginBaseController;
import org.onetwo.plugins.admin.WebAdminPlugin;
import org.onetwo.plugins.admin.vo.AdminLoginUserInfo;
import org.springframework.beans.factory.annotation.Autowired;

//@PluginContext(contextPath="/web-admin")
abstract public class WebAdminBaseController extends PluginBaseController implements DateInitBinder {

	@Autowired
	private WebAdminPlugin webAdminPlugin;

	@Override
	protected WebPlugin getPlugin() {
		return webAdminPlugin;
	}

	/***
	 * 注意，这里获取当前登录用户信息的时候写死了类型为AdminLoginUserInfo，
	 * 如果当前系统自定义了userDetail，会因为类型不一致的问题导致提示没有登录
	 */
	protected AdminLoginUserInfo checkAndGetCurrentLoginUser(){
		return checkAndGetCurrentLoginUser(AdminLoginUserInfo.class, true);
	}
	
}
