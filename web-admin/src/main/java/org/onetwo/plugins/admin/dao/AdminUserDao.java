
package org.onetwo.plugins.admin.dao;

import java.util.List;

import org.onetwo.common.db.dquery.annotation.DbmRepository;
import org.onetwo.plugins.admin.entity.AdminUser;
import org.onetwo.plugins.admin.vo.FindUserByRoleQuery;

@DbmRepository
public interface AdminUserDao {
    
	/***
	 * 根据用户id和名称查找用户
	 * @author weishao zeng
	 * @param query
	 * @return
	 */
	List<AdminUser> findUserByRole(FindUserByRoleQuery query);
	

	List<AdminUser> findUserByIds(List<Long> ids);
	
	
}