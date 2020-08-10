package org.onetwo.plugins.admin.event;
/**
 * @author weishao zeng
 * <br/>
 */

import java.io.Serializable;
import java.util.List;

import org.onetwo.plugins.admin.vo.RoleVO;

import lombok.Data;

/***
 * 
 * class UserRoleAssignedListener {
 * 		@TransactionalEventListener
 * 		void onAssign(UserRoleAssignedEvent event) {
 * 		}
 * }
 * 
 * @author way
 *
 */
@SuppressWarnings("serial")
@Data
public class UserRoleAssignedEvent implements Serializable {
	/***
	 * 被分配角色的用户id
	 */
	Long userId;
	
	/****
	 * 被分配的角色
	 */
	List<RoleVO> roles;
	
}
