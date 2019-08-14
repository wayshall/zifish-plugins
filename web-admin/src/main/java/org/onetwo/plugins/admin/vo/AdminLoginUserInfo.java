package org.onetwo.plugins.admin.vo;

import java.util.Collection;

import org.onetwo.ext.security.utils.LoginUserDetails;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

/**
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
public class AdminLoginUserInfo extends LoginUserDetails {

	@Setter
	@Getter
	private Long tenantId;
	
	public AdminLoginUserInfo(long userId, String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(userId, username, password, authorities);
	}

}

