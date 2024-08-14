package org.onetwo.plugins.admin.controller;

import org.onetwo.boot.core.web.view.XResponseView;
import org.onetwo.ext.permission.api.annotation.ByPermissionClass;
import org.onetwo.plugins.admin.service.AdminLoginUserResponseProcessor;
import org.onetwo.plugins.admin.vo.AdminLoginUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@XResponseView
public class AdminMeController extends WebAdminBaseController {
	
	@Autowired
	private AdminLoginUserResponseProcessor loginUserResponseProcessor;
	
	/*****
	 * 注意，这里获取当前登录用户信息的时候写死了类型为AdminLoginUserInfo，
	 * 如果当前系统自定义了userDetail，会因为类型不一致的问题导致提示没有登录
	 * @author weishao zeng
	 * @return
	 */
	@GetMapping("me")
	@ByPermissionClass
	public Object me(){
		AdminLoginUserInfo userDetail = this.checkAndGetCurrentLoginUser();
		Object user = this.loginUserResponseProcessor.apply(userDetail);
		return user;
	}
	
}
