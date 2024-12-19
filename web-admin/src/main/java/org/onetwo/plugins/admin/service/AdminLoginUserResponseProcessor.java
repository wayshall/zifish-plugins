package org.onetwo.plugins.admin.service;

import org.onetwo.ext.security.utils.AdminLoginUserInfo;

/**
 * @author weishao zeng
 * <br/>
 */
public interface AdminLoginUserResponseProcessor {
	
	Object apply(AdminLoginUserInfo userDetail);

}
