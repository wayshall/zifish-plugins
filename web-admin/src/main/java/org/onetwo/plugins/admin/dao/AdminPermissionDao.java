
package org.onetwo.plugins.admin.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.onetwo.common.db.dquery.annotation.DbmRepository;
import org.onetwo.common.db.dquery.annotation.Param;
import org.onetwo.plugins.admin.entity.AdminPermission;

@DbmRepository
public interface AdminPermissionDao {

	List<AdminPermission> findAppPermissionsByUserId(@Param("appCode")String appCode, @Param("userId")long userId);
	
	default List<AdminPermission> findAppPermissionsByRoleIds(@Param("appCode")String appCode, @Param("roleId")long roleId) {
		return findAppPermissionsByRoleIds(appCode, Arrays.asList(roleId));
	}
	
	List<AdminPermission> findAppPermissionsByRoleIds(@Param("appCode")String appCode, @Param("roleIds")Collection<Long> roleIds);
	
	/***
	 * 根据appcode查找权限
	 * @author wayshall
	 * @param appCode
	 * @return
	 */
	List<AdminPermission> findAppPermissions(@Param("appCode")String appCode);
	/***
	 * 根据多个appcode查找权限
	 * @author wayshall
	 * @param codes
	 * @return
	 */
	List<AdminPermission> findPermissions(@Param("codes")Collection<String> codes);
	
	/****
	 * 根据代码删除角色权限关联数据
	 * @author weishao zeng
	 * @param permissionCode
	 * @param usePostLike
	 * @return
	 */
	int deleteRolePermissions(@Param("permissionCode")String permissionCode, boolean usePostLike);
	int deletePermission(@Param("code")String code, boolean usePostLike);
	
}