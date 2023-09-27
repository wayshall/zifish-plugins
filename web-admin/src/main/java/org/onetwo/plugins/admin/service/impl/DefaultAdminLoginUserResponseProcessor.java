package org.onetwo.plugins.admin.service.impl;

import java.util.stream.Collectors;

import org.onetwo.common.spring.copier.CopyUtils;
import org.onetwo.plugins.admin.entity.AdminUserAudit;
import org.onetwo.plugins.admin.service.AdminLoginUserResponseProcessor;
import org.onetwo.plugins.admin.utils.WebAdminProperties;
import org.onetwo.plugins.admin.utils.WebAdminProperties.LoginUserProps;
import org.onetwo.plugins.admin.vo.AdminLoginUserInfo;
import org.onetwo.plugins.admin.vo.AdminLoginUserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author weishao zeng
 * <br/>
 */
@Transactional
public class DefaultAdminLoginUserResponseProcessor implements AdminLoginUserResponseProcessor {

	@Autowired
	private AdminUserAuditServiceImpl adminAuditService;
	@Autowired
	private WebAdminProperties webAdminProperties;
	
	@Override
	public Object apply(AdminLoginUserInfo userDetail) {
		AdminLoginUserResponse user = CopyUtils.copyFrom(userDetail)
										.propertyMapping("nickName", "nickname")
										.ignoreFields("authorities")
				 						.toClass(AdminLoginUserResponse.class);
		
		if (webAdminProperties.isForceModifyPassword()) {
			AdminUserAudit audit = adminAuditService.findById(userDetail.getUserId());
			user.setChangedPassword(audit!=null && audit.getLastChangePwdAt()!=null);
		} else {
			user.setChangedPassword(true);
		}
		LoginUserProps loginUser = webAdminProperties.getLoginUser();
		if (loginUser.isExposeAuthorities()) {
			user.setAuthorities(userDetail.getAuthorities().stream().map(g -> g.getAuthority()).collect(Collectors.toList()));
		}
		/*if (userDetail instanceof UserRoot) {
			user.setSystemRootUser(((UserRoot)userDetail).isSystemRootUser());
		}*/
		user.setSystemRootUser(userDetail.isSystemRootUser());
		return user;
	}

}
