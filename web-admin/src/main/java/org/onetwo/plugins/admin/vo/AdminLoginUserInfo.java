package org.onetwo.plugins.admin.vo;

import java.util.Collection;

import org.onetwo.ext.security.utils.LoginUserDetails;
import org.springframework.security.core.GrantedAuthority;

import lombok.Setter;

/**
 * 默认使用admin_organ的规则，admin_organ实际上是租户，此时企业id和租户id是相同的
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
		super(userId, username, password, authorities);
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

