package org.onetwo.plugins.admin.service.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.exception.ServiceException;
import org.onetwo.common.spring.copier.CopyUtils;
import org.onetwo.plugins.admin.entity.AdminUser;
import org.onetwo.plugins.admin.entity.AdminUserBinding;
import org.onetwo.plugins.admin.entity.AdminUserBinding.BindingUserId;
import org.onetwo.plugins.admin.vo.CreateOrUpdateAdminUserRequest;
import org.onetwo.plugins.admin.vo.UserBindingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Deprecated
@Service
@Transactional
public class AdminUserBindingService {
    @Autowired
    private BaseEntityManager baseEntityManager;

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
	public void createOrUpdateAdminUser(CreateOrUpdateAdminUserRequest adminUser, AdminUser dbUser) {
		UserBindingRequest binding = new UserBindingRequest();
		binding.setAdminUserId(dbUser.getId());
		binding.setAvatar(adminUser.getAvatar());
		binding.setBindingUserName(dbUser.getUserName());
		binding.setBindingUserId(adminUser.getBindingUserId());
		bindingUser(binding, true);
	}
    
    public AdminUser loadById(Long id){
    	AdminUser user = baseEntityManager.load(AdminUser.class, id);
    	return user;
    }
    
	/***
     * 绑定
     * @author weishao zeng
     * @param bindingRequest
     */
    public AdminUserBinding bindingUser(UserBindingRequest bindingRequest, boolean forceBinding) {
    	if (bindingRequest.getBindingUserId()==null) {
    		throw new ServiceException("绑定的用户id不能为空！");
    	}
    	AdminUser adminUser = this.loadById(bindingRequest.getAdminUserId());
    	if (adminUser.isSystemRootUser()) {
    		throw new ServiceException("终极管理员只做系统维护使用，无法绑定！");
    	}
    	AdminUserBinding binding = getBinding(bindingRequest.getAdminUserId());
    	if (binding!=null) {
    		if (forceBinding) {
    			unbinding(binding.getId());
    		} else {
    			throw new ServiceException("该后台用户已绑定过其它用户").putAsMap(bindingRequest);
    		}
    	}
    	
    	binding = CopyUtils.copy(AdminUserBinding.class, bindingRequest);
    	binding.setBindingAt(new Date());
    	baseEntityManager.persist(binding);
    	
    	boolean userUpdated = false;
    	if (StringUtils.isBlank(adminUser.getNickName())) {
        	adminUser.setNickName(binding.getBindingUserName());
        	userUpdated = true;
    	}
    	if (StringUtils.isNotBlank(bindingRequest.getAvatar())) {
    		adminUser.setAvatar(bindingRequest.getAvatar());
        	userUpdated = true;
    	}
    	if(userUpdated) {
    		baseEntityManager.update(adminUser);
    	}
    	
    	return binding;
    }
    
    public AdminUserBinding getBinding(Long adminUserId) {
    	AdminUserBinding binding = this.baseEntityManager.findUnique(AdminUserBinding.class, "adminUserId", adminUserId);
    	return binding;
    }
    
    public AdminUserBinding unbinding(BindingUserId id) {
    	AdminUserBinding binding = this.baseEntityManager.removeById(AdminUserBinding.class, id);
    	return binding;
    }
}
