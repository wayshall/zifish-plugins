package org.onetwo.plugins.admin.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.db.sqlext.ExtQuery.K;
import org.onetwo.common.db.sqlext.ExtQuery.K.IfNull;
import org.onetwo.common.exception.BaseException;
import org.onetwo.common.exception.ServiceException;
import org.onetwo.common.spring.copier.CopyUtils;
import org.onetwo.common.utils.StringUtils;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.ext.permission.AbstractPermissionManager;
import org.onetwo.ext.permission.api.DataFrom;
import org.onetwo.ext.permission.parser.MenuInfoParser;
import org.onetwo.ext.permission.utils.PermissionUtils;
import org.onetwo.plugins.admin.dao.AdminPermissionDao;
import org.onetwo.plugins.admin.entity.AdminApplication;
import org.onetwo.plugins.admin.entity.AdminPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

//@Service
@Transactional
public class PermissionManagerImpl extends AbstractPermissionManager<AdminPermission> {

    @Autowired
    private BaseEntityManager baseEntityManager;
	
	@Resource
	private AdminPermissionDao adminPermissionDao;
	
	public PermissionManagerImpl() {
	}
	
	public AdminPermission delete(String code) {
		AdminPermission dbPermission = baseEntityManager.load(AdminPermission.class, code);
		if (dbPermission.getChildrenSize()>0) {
			throw new ServiceException("该权限又子节点，无法删除，请先删除子节点！");
		}
		if (StringUtils.isNotBlank(dbPermission.getParentCode())) {
			AdminPermission parent = findByCode(dbPermission.getParentCode());
			parent.setChildrenSize(parent.getChildrenSize()-1);
			baseEntityManager.update(parent);
		}
		baseEntityManager.remove(dbPermission);
		return dbPermission;
	}
	
	/*private int countChildrenSize(String code) {
		return Querys.from(baseEntityManager, AdminPermission.class)
					.where()
						.field("parentCode").is(code)
					.toQuery().count().intValue();
	}*/
	
	public AdminPermission persist(AdminPermission permission) {
		AdminPermission dbPermission = findByCode(permission.getCode());
		if (dbPermission!=null) {
			throw new ServiceException("权限代码重复：" + permission.getCode());
		}
		AdminPermission parent = this.checkParent(permission);
		if (parent!=null) {
			if (!permission.getCode().startsWith(parent.getCode())) {
				throw new ServiceException("子权限代码必须以父权限代码为前缀：" + parent.getCode());
			}
			parent.setChildrenSize(parent.getChildrenSize()+1);
			baseEntityManager.update(parent);
		}
		this.baseEntityManager.persist(permission);
		return permission;
	}
	
	public AdminPermission update(AdminPermission permission) {
		AdminPermission parent = this.checkParent(permission);
		AdminPermission dbPermission = findByCode(permission.getCode());
		String oldParentCode = dbPermission.getParentCode();
		CopyUtils.copyIgnoreNullAndBlank(dbPermission, permission);
		this.baseEntityManager.update(dbPermission);
		// 有传parent_code，并且和数据的不同，维护childrenSize
		if (parent!=null && StringUtils.isNotBlank(oldParentCode) && !parent.getCode().equals(oldParentCode)) {
			AdminPermission oldParent = findByCode(oldParentCode);
			oldParent.setChildrenSize(oldParent.getChildrenSize()-1);
			baseEntityManager.update(oldParent);
			
			parent.setChildrenSize(parent.getChildrenSize()+1);
			baseEntityManager.update(parent);
		}
		return dbPermission;
	}
	
	private AdminPermission checkParent(AdminPermission permission) {
		if (StringUtils.isNotBlank(permission.getParentCode())) {
			AdminPermission parent = findByCode(permission.getParentCode());
			if (parent==null) {
				throw new ServiceException("找不到父节点：" + permission.getParentCode());
			}
			return parent;
		}
		return null;
	}

	@Override
    public AdminPermission findByCode(String code) {
		AdminPermission ap = this.baseEntityManager.findById(AdminPermission.class, code);
		if(ap==null)
			return null;
		return ap;
    }
	

	@Override
	protected Map<String, AdminPermission> findExistsPermission(String rootCode) {
		List<AdminPermission> adminPermissions = this.baseEntityManager.findList(AdminPermission.class, "code:like", rootCode+"%");
		Map<String, AdminPermission> dbPermissions = adminPermissions.stream()
													.collect(Collectors.toMap(p->p.getCode(), p->p));
		
		return dbPermissions;
	}


	@Override
	protected void updatePermissions(AdminPermission rootPermission, Map<String, AdminPermission> dbPermissionMap, Set<AdminPermission> adds, Set<AdminPermission> deletes, Set<AdminPermission> updates) {
		AdminApplication app = this.baseEntityManager.findById(AdminApplication.class, rootPermission.getAppCode());
		if(app==null){
			app = new AdminApplication();
			app.setCode(rootPermission.getAppCode());
			app.setName(rootPermission.getName());
			app.setCreateAt(new Date());
			app.setUpdateAt(new Date());
			this.baseEntityManager.persist(app);
		}else{
			app.setName(rootPermission.getName());
			app.setUpdateAt(new Date());
			this.baseEntityManager.update(app);
		}
		
		logger.info("adds[{}]: {}", adds.size(), adds);
		adds.forEach(p->{
			/*p.setCreateAt(new Date());
			p.setUpdateAt(new Date());*/
			this.baseEntityManager.persist(p);
		});

		logger.info("deletes[{}]: {}", deletes.size(), deletes);
		deletes.stream().filter(p->p.getDataFrom()==DataFrom.SYNC).forEach(p->{
			this.adminPermissionDao.deleteRolePermissions(p.getCode());
			this.baseEntityManager.remove(p);
		});

		logger.info("updates[{}]: {}", updates.size(), updates);
		updates.stream().filter(p->p.getDataFrom()==DataFrom.SYNC).forEach(p->{
//			this.adminPermissionMapper.updateByPrimaryKey(p.getAdminPermission());
			AdminPermission dbPermission = dbPermissionMap.get(p.getCode());
			CopyUtils.copier().from(p).ignoreNullValue().ignoreBlankString().to(dbPermission);
			this.baseEntityManager.update(p);
		});
	}

	@Override
	protected void removeRootMenu(MenuInfoParser<AdminPermission> menuInfoParser){
		String appCode = menuInfoParser.getRootMenuParser().getAppCode();
		baseEntityManager.removeById(AdminApplication.class, appCode);
	}

	@Override
	@Transactional(readOnly=true)
	public List<AdminPermission> findAppMenus(String appCode){
//		List<IMenu> menulist = (List<IMenu>)baseEntityManager.findByProperties(this.menuInfoParser.getMenuInfoable().getIMenuClass(), "appCode", appCode);
		List<AdminPermission> permList = findAppPermissions(appCode);
		return permList.stream()
						.filter(p->PermissionUtils.isMenu(p))
						.collect(Collectors.toList());
	}

//	@Override
	@Transactional(readOnly=true)
	public List<AdminPermission> findAppPermissions(String appCode){
		List<AdminPermission> permList = baseEntityManager.findList(AdminPermission.class, "appCode", appCode, K.IF_NULL, IfNull.Ignore, K.ASC, "sort");
		if(permList.isEmpty())
			throw new BaseException("没有任何权限……");
		return permList;
	}

//	@Override
	public List<AdminPermission> findPermissionByCodes(String appCode, String[] permissionCodes) {
		return baseEntityManager.findList(AdminPermission.class, "appCode", appCode, "code:in", permissionCodes,  K.IF_NULL, IfNull.Ignore);
	}

	@Override
	public List<AdminPermission> findUserAppMenus(String appCode, UserDetail userDetail) {
		List<AdminPermission> adminPermissions = this.adminPermissionDao.findAppPermissionsByUserId(appCode, userDetail.getUserId());
		List<AdminPermission> permList = adminPermissions.stream()
				.filter(p->PermissionUtils.isMenu(p))
				.collect(Collectors.toList());
		return permList;
	}

}
