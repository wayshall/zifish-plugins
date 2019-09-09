package org.onetwo.plugins.admin.event;

import org.onetwo.common.utils.LangUtils;
import org.onetwo.plugins.admin.entity.AdminUser;
import org.onetwo.plugins.admin.service.impl.AdminRoleServiceImpl;
import org.onetwo.plugins.admin.service.impl.AdminUserServiceImpl;
import org.onetwo.plugins.admin.vo.CreateOrUpdateAdminUserRequest;
import org.onetwo.plugins.admin.vo.UserBindingRequest;
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
	@Autowired
	private AdminRoleServiceImpl adminRoleService;
	
	@Override
	public void onApplicationEvent(CreateOrUpdateAdminUserEvent event) {
		if (LangUtils.isEmpty(event.getAdminUsers())) {
			log.warn("no admin user found!");
			return ;
		}
		for(CreateOrUpdateAdminUserRequest adminUser : event.getAdminUsers()) {
			AdminUser dbUser = adminUserService.findByUserName(adminUser.getUserName());
			if (dbUser==null) {
				log.info("create new admin user: {}", adminUser);
				dbUser = adminUser.asBean(AdminUser.class);
				adminUserService.save(dbUser, null);
				
				UserBindingRequest binding = new UserBindingRequest();
				binding.setAdminUserId(dbUser.getId());
				binding.setAvatar(adminUser.getAvatar());
				binding.setBindingUserName(dbUser.getUserName());
				binding.setBindingUserId(adminUser.getBindingUserId());
				adminUserService.bindingUser(binding, true);
			} else {
				log.info("admin user[{}] has exists!", adminUser.getUserName());
//				continue;
			}
			if (LangUtils.isNotEmpty(adminUser.getRoleIds())) {
				adminRoleService.saveUserRoles(dbUser.getId(), adminUser.getRoleIds().toArray(new Long[0]));
			}
		}
	}

}
