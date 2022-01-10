package org.onetwo.plugins.admin.utils;

import java.util.Optional;

import org.onetwo.common.db.spi.QueryContextVariable.QueryGlobalVariable;
import org.onetwo.common.web.userdetails.GenericUserDetail;
import org.onetwo.common.web.userdetails.SessionUserManager;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.plugins.admin.vo.AdminLoginUserInfo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author weishao zeng
 * <br/>
 */
public class AdminTenantContextVariable implements QueryGlobalVariable {
	private static final String NAME = "_tenant";
	@Autowired
	private SessionUserManager<GenericUserDetail<?>> sessionUserManager;
	@Override
	public String varName() {
		return NAME;
	}
	
	public Long getTenantId() {
    	Optional<AdminLoginUserInfo> adminUser = getAdminUser();
    	if (!adminUser.isPresent()) {
            return null;
    	}
		return adminUser.get().getTenantId();
	}
	
	public String getClientId() {
		throw new UnsupportedOperationException("clientId not support!");
//		return null;
	}

    private Optional<AdminLoginUserInfo> getAdminUser() {
    	UserDetail user = (UserDetail)sessionUserManager.getCurrentUser();
    	if (user==null || !(user instanceof AdminLoginUserInfo)) {
            return Optional.empty();
    	}
    	AdminLoginUserInfo adminUser = (AdminLoginUserInfo) user;
    	return Optional.of(adminUser);
    }
}

