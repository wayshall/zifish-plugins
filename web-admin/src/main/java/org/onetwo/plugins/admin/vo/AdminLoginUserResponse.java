package org.onetwo.plugins.admin.vo;

import java.util.Collection;
import java.util.List;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
public class AdminLoginUserResponse {

	Long userId;
	String nickName;
	String userName;
	String avatar;
	List<String> roles;
	Long organId;
	boolean systemRootUser;
	/***
	 * 是否有修改过密码
	 */
	boolean changedPassword;
	Long tenantId;
	
	Collection<String> authorities;

}
