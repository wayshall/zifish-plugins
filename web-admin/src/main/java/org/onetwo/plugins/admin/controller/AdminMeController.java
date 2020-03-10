package org.onetwo.plugins.admin.controller;

import java.util.List;

import org.onetwo.common.spring.copier.CopyUtils;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.plugins.admin.entity.AdminUserAudit;
import org.onetwo.plugins.admin.service.impl.AdminUserAuditServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@RestController
public class AdminMeController extends WebAdminBaseController {
	
	@Autowired
	private AdminUserAuditServiceImpl adminAuditService;
	
	/*****
	 * 注意，这里获取当前登录用户信息的时候写死了类型为AdminLoginUserInfo，
	 * 如果当前系统自定义了userDetail，会因为类型不一致的问题导致提示没有登录
	 * @author weishao zeng
	 * @return
	 */
	@GetMapping("me")
	public AdminUserInfo me(){
		UserDetail userDetail = this.checkAndGetCurrentLoginUser();
		AdminUserAudit audit = adminAuditService.findById(userDetail.getUserId());
		AdminUserInfo user = CopyUtils.copyFrom(userDetail)
										.propertyMapping("nickName", "nickname")
				 						.toClass(AdminUserInfo.class);
		user.setChangedPassword(audit.getLastChangePwdAt()!=null);
		/*if (userDetail instanceof UserRoot) {
			user.setSystemRootUser(((UserRoot)userDetail).isSystemRootUser());
		}*/
		return user;
	}
	
	@Data
	public static class AdminUserInfo {
		Long userId;
		String nickName;
		String userName;
		String avatar;
		List<String> roles;
		Long organId;
		boolean systemRootUser;
		/***
		 * 是否有修改过密码
		 */
		boolean changedPassword;
	}

}
