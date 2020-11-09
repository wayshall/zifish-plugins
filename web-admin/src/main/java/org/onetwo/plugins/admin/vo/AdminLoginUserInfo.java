package org.onetwo.plugins.admin.vo;

import java.util.Collection;
import java.util.Collections;

import org.onetwo.ext.security.utils.LoginUserDetails;
import org.springframework.security.core.GrantedAuthority;

import lombok.Setter;

/**
 * 移除admin_organ，用户表增加tenant_id和保留organId用于业务扩展
 * 
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
public class AdminLoginUserInfo extends LoginUserDetails {

	@Setter
	private Long bindingUserId;
	
	@Setter
	private Long organId;
	@Setter
	private Long tenantId;
	
	public AdminLoginUserInfo(long userId, String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(userId, username, password, authorities==null?Collections.emptyList():authorities);
	}

	
	public Long getOrganId() { 
		return organId; 
	}

	public Long getBindingUserId() {
		return bindingUserId;
	}

	public Long getTenantId() {
		return tenantId;
	}

}

