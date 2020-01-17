package org.onetwo.plugins.admin.event;

import org.onetwo.common.utils.LangUtils;
import org.onetwo.plugins.admin.service.impl.AdminUserServiceImpl;
import org.onetwo.plugins.admin.vo.CreateOrUpdateAdminUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import lombok.extern.slf4j.Slf4j;

/**
 * @author weishao zeng
 * <br/>
 */
@Slf4j
public class CreateOrUpdateAdminUserListenner implements ApplicationListener<CreateOrUpdateAdminUserEvent>{
	
	@Autowired
	private AdminUserServiceImpl adminUserService;
	
	@Override
	public void onApplicationEvent(CreateOrUpdateAdminUserEvent event) {
		if (LangUtils.isEmpty(event.getAdminUsers())) {
			log.warn("no admin user found!");
			return ;
		}
		for(CreateOrUpdateAdminUserRequest adminUser : event.getAdminUsers()) {
			this.adminUserService.createOrUpdateAdminUser(adminUser);
		}
	}
	
	

}
