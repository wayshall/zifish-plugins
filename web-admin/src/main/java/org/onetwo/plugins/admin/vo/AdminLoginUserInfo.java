package org.onetwo.plugins.admin.vo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.ext.security.utils.GenericLoginUserDetails;
import org.springframework.security.core.GrantedAuthority;

import lombok.Setter;

/**
 * 移除admin_organ，用户表增加tenant_id和保留organId用于业务扩展
 * 
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
public class AdminLoginUserInfo extends GenericLoginUserDetails<Long> implements UserDetail {

	@Setter
	private Long bindingUserId;
	
	@Setter
	private Long organId;
	@Setter
	private Long tenantId;
	
	private List<String> roles;
	
	public AdminLoginUserInfo(long userId, String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(userId, username, password, authorities==null?Collections.emptyList():authorities);
	}
	
	public boolean isAdminRole() {
		return this.roles!=null && this.roles.contains("ADMIN");
	}
	
	public boolean isRole(String roleCode) {
		return this.roles!=null && this.roles.contains(roleCode);
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

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

}

