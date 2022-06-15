package org.onetwo.plugins.admin.vo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.common.web.userdetails.UserTypes;
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
public class AdminLoginUserInfo extends GenericLoginUserDetails<Long> implements UserDetail, Cloneable {
	public static final String ROLE_ADMIN = "ADMIN";

//	@Setter
//	private Long bindingUserId;

	@Setter
	private Long organId;
	@Setter
	private Long tenantId;
	
	private List<String> roles;
	
	protected AdminLoginUserInfo(AdminLoginUserInfo loginUser) {
		super(loginUser.getUserId(), loginUser.getUserName(), loginUser.getPassword(), loginUser.getAuthorities());
		this.organId = loginUser.getOrganId();
		this.tenantId = loginUser.getTenantId();
		this.roles = loginUser.getRoles();
	}
	
	public AdminLoginUserInfo(long userId, String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(userId, username, password, authorities==null?Collections.emptyList():authorities);
	}
	
	public boolean isAdminRole() {
		return this.roles!=null && this.roles.contains(ROLE_ADMIN);
	}
	
	public boolean isRole(String roleCode) {
		return this.roles!=null && this.roles.contains(roleCode);
	}

	public Long getOrganId() { 
		return organId; 
	}

//	public Long getBindingUserId() {
//		return bindingUserId;
//	}
//	
//	public void setBindingUserId(Long bindingUserId) {
//		this.bindingUserId = bindingUserId;
//	}

	public Long getTenantId() {
		return tenantId;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public UserTypes getUserType() {
		return UserTypes.ADMIN_USER;
	}
	
	
}

