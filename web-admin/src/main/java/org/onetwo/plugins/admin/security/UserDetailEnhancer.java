package org.onetwo.plugins.admin.security;
/**
 * @author weishao zeng
 * <br/>
 */

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailEnhancer {
	
	UserDetails enhance(UserDetails userDetail);

}
