package org.onetwo.plugins.admin.service;

import org.onetwo.plugins.admin.vo.AdminLoginUserInfo;

/**
 * @author weishao zeng
 * <br/>
 */
public interface AdminLoginUserResponseProcessor {
	
	Object apply(AdminLoginUserInfo userDetail);

}
