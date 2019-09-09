package org.onetwo.plugins.admin.vo;

import java.util.List;

import org.onetwo.plugins.admin.entity.AdminRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleResponse {

	List<AdminRole> roles;
	/***
	 * 用户当前所属角色id
	 */
	List<Long> userRoleIds;
	
}

