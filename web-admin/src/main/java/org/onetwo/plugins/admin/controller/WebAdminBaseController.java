package org.onetwo.plugins.admin.controller;

import org.onetwo.boot.core.web.controller.DateInitBinder;
import org.onetwo.boot.plugin.core.WebPlugin;
import org.onetwo.boot.plugin.mvc.PluginBaseController;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.plugins.admin.WebAdminPlugin;
import org.onetwo.plugins.admin.vo.AdminLoginUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

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

	// 获取当前登录的用户信息，但是没有登录，或者游客模式，也不会抛错
	@ModelAttribute(name = UserDetail.USER_DETAIL_KEY)
	public AdminLoginUserInfo getCurrentLoginUser() {
		return checkAndGetCurrentLoginUser(AdminLoginUserInfo.class, false);
	}

}
