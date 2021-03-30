
package org.onetwo.plugins.admin.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.onetwo.common.db.builder.Querys;
import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.db.sqlext.ExtQuery.K;
import org.onetwo.common.db.sqlext.ExtQuery.K.IfNull;
import org.onetwo.common.exception.ServiceException;
import org.onetwo.common.spring.copier.CopyUtils;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.utils.Page;
import org.onetwo.plugins.admin.dao.AdminPermissionDao;
import org.onetwo.plugins.admin.dao.AdminRoleDao;
import org.onetwo.plugins.admin.entity.AdminPermission;
import org.onetwo.plugins.admin.entity.AdminRole;
import org.onetwo.plugins.admin.event.UserRoleAssignedEvent;
import org.onetwo.plugins.admin.utils.Enums.CommonStatus;
import org.onetwo.plugins.admin.vo.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.google.common.collect.Sets;

@Service
@Transactional
public class AdminRoleServiceImpl {

    @Autowired
    private BaseEntityManager baseEntityManager;
    @Autowired
    private AdminRoleDao adminRoleDao;
    
    @Autowired
    private AdminPermissionDao adminPermissionDao;
    @Autowired
    private ApplicationContext applicationContext;

	@Transactional(readOnly=true)
	public List<AdminPermission> findAppPermissions(String appCode){
		List<AdminPermission> permList = baseEntityManager.findList(AdminPermission.class, 
																	"appCode", appCode, 
																	K.IF_NULL, IfNull.Ignore, 
																	K.ASC, "sort");
		if(permList.isEmpty())
			throw new ServiceException("没有任何权限……");
		return permList;
	}
	
    public void findPage(Page<AdminRole> page, AdminRole adminRole){
//        baseEntityManager.findPage(AdminRole.class, page);
    	Querys.from(baseEntityManager, AdminRole.class)
		.where()
    		.field("name").like(adminRole.getName())
    		.field("status").equalTo(adminRole.getStatus())
    		.ignoreIfNull()
		.end()
		.toQuery()
		.page(page);
    }
    
    public List<AdminRole> findByStatus(CommonStatus status, Long tenantId){
    	return baseEntityManager.findList(AdminRole.class, 
    								"status", status, 
    								"tenantId", tenantId, 
    								K.IF_NULL, IfNull.Ignore);
    }
    
    public void save(AdminRole adminRole){
    	int count = this.baseEntityManager.countRecord(AdminRole.class, 
						"name", adminRole.getName())
						.intValue();
		if(count>0){
			throw new ServiceException("添加失败：["+adminRole.getName()+"]的角色已存在，请检查！");
		}
        Date now = new Date();
        adminRole.setCreateAt(now);
        adminRole.setUpdateAt(now);
//        if (adminRole.getOrganId()==null) {
//        	adminRole.setOrganId(0L);
//        }
        if (adminRole.getTenantId()==null) {
        	adminRole.setTenantId(0L);
        }
        baseEntityManager.save(adminRole);
    }
    
    public AdminRole loadById(Long id){
        return baseEntityManager.load(AdminRole.class, id);
    }
    
    public void update(AdminRole adminRole){
        Assert.notNull(adminRole.getId(), "参数不能为null");
        int count = this.baseEntityManager.countRecord(AdminRole.class, 
													"name", adminRole.getName(), 
													"id:!=", adminRole.getId())
													.intValue();
		if(count>0){
			throw new ServiceException("更新失败：["+adminRole.getName()+"]的角色已存在，请检查！");
		}

        AdminRole dbAdminRole = loadById(adminRole.getId());
        CopyUtils.copyFrom(adminRole)
        		.ignoreNullValue()
        		.to(dbAdminRole);
        dbAdminRole.setUpdateAt(new Date());
        baseEntityManager.update(dbAdminRole);
    }
    
    public void deleteByIds(Long...ids){
        if(ArrayUtils.isEmpty(ids))
            throw new ServiceException("请先选择数据！");
        Stream.of(ids).forEach(id->deleteById(id));
    }
    
    public void deleteById(Long id){
        AdminRole adminRole = loadById(id);
        if(adminRoleDao.countRolePermisssion(adminRole.getAppCode(), adminRole.getId())>0){
        	throw new ServiceException("该角色有权限关联，无法删除！");
        }
        if(adminRoleDao.countUserRole(adminRole.getId())>0){
        	throw new ServiceException("该角色有用户关联，无法删除！");
        }
//        adminRole.setStatus(CommonStatus.DELETE.name());
//        baseEntityManager.update(adminRole);
        baseEntityManager.remove(adminRole);
    }
    
    /***
     * 查找用户的角色code 
     * @author weishao zeng
     * @param userId
     * @return
     */
    public List<String> findRoleCodesByUser(long userId){
    	return findRolesByUser(userId)
							.stream()
							.map(r -> {
								String code = r.getCode();
								if (StringUtils.isBlank(code)) {
									code = r.getName();
								}
								return code;
							})
							.collect(Collectors.toList());
    }
    
    public List<AdminRole> findRolesByUser(long userId){
    	return this.adminRoleDao.findRolesByUser(userId);
    }
    
    public List<Long> findRoleIdsByUser(long userId){
    	List<AdminRole> roles = findRolesByUser(userId);
    	return roles.stream().map(r->r.getId())
    						 .collect(Collectors.toList());
    }
    
    public void saveUserRoles(long userId, Long... roleIds){
    	this.adminRoleDao.deleteUserRoles(userId);
    	if(LangUtils.isEmpty(roleIds)){
    		return ;
    	}
    	
    	UserRoleAssignedEvent event = new UserRoleAssignedEvent();
    	event.setUserId(userId);
    	List<RoleVO> assignedRoles = new ArrayList<>();
    	for (Long roleId : roleIds) {
    		AdminRole role = loadById(roleId);
    		RoleVO roleVo = CopyUtils.copy(RoleVO.class, role);
    		assignedRoles.add(roleVo);
    	}
    	event.setRoles(assignedRoles);
    	
    	Stream.of(roleIds).forEach(roleId->adminRoleDao.insertUserRole(userId, roleId));
    	
    	this.applicationContext.publishEvent(event);
    }
    
    public List<String> findRolePermissionsByRoleId(long roleId) {
    	AdminRole role = loadById(roleId);
    	if(CommonStatus.valueOf(role.getStatus())==CommonStatus.DELETE){
    		throw new ServiceException("角色已删除");
    	}
    	List<String> rolePerms = this.adminPermissionDao.findAppPermissionsByRoleIds(role.getAppCode(), roleId)
    													.stream()
    													.map(p->p.getCode())
				 										.collect(Collectors.toList());
		/*List<AdminPermission> allPerms = findAppPermissions(role.getAppCode());
		
		RolePermissionReponse res = RolePermissionReponse.builder()
								.rolePerms(rolePerms)
								.allPerms(allPerms)
								.build();*/
		return rolePerms;
    }
    

   /* public List<String> findAppPermissionCodesByRoleIds(String appCode, long roleId){
    	AdminRole role = loadById(roleId);
    	if(CommonStatus.valueOf(role.getStatus())==CommonStatus.DELETE){
    		throw new ServiceException("角色已删除");
    	}
    	List<AdminPermission> perms = this.adminPermissionDao.findAppPermissionsByRoleIds(appCode, roleId);
    	return perms.stream().map(p->p.getCode())
    						 .collect(Collectors.toList());
	}*/
    
    public void saveRolePermission(long roleId, String...assignPerms){
    	AdminRole role = loadById(roleId);
    	if(CommonStatus.valueOf(role.getStatus())==CommonStatus.DELETE){
    		throw new ServiceException("角色已删除");
    	}
    	String appCode = role.getAppCode();
    	if(LangUtils.isEmpty(assignPerms)){
    		this.adminRoleDao.deleteRolePermisssion(appCode, roleId, null);
    		return ;
    	}
    	List<String> existsPermCodes = this.adminRoleDao.findRolePermisssion(appCode, roleId);
    	
    	Set<String> adds = Sets.difference(Sets.newHashSet(assignPerms), Sets.newHashSet(existsPermCodes));
    	adds.stream().forEach(code->adminRoleDao.insertRolePermission(roleId, code));
    	
		Set<String> deletes = Sets.difference(Sets.newHashSet(existsPermCodes), Sets.newHashSet(assignPerms));
		deletes.stream().forEach(code->adminRoleDao.deleteRolePermisssion(appCode, roleId, code));
    }
    
    public AdminRole findByName(String roleName){
    	return Querys.from(baseEntityManager, AdminRole.class)
    					.where()
    					.field("name").equalTo(roleName)
    					.end()
    					.toQuery()
    					.one();
    }
	
}