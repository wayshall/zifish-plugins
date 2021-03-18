
package org.onetwo.plugins.admin.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.onetwo.boot.core.web.service.BootCommonService;
import org.onetwo.common.db.builder.Querys;
import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.exception.ServiceException;
import org.onetwo.common.file.FileStoredMeta;
import org.onetwo.common.reflect.ReflectUtils;
import org.onetwo.common.utils.Assert;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.utils.Page;
import org.onetwo.common.utils.StringUtils;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.dbm.dialet.DBDialect.LockInfo;
import org.onetwo.ext.security.utils.LoginUserDetails;
import org.onetwo.plugins.admin.dao.AdminRoleDao;
import org.onetwo.plugins.admin.dao.AdminUserDao;
import org.onetwo.plugins.admin.entity.AdminUser;
import org.onetwo.plugins.admin.vo.CreateOrUpdateAdminUserRequest;
import org.onetwo.plugins.admin.vo.FindUserByRoleQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class AdminUserServiceImpl {

    @Autowired
    private BaseEntityManager baseEntityManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AdminRoleDao adminRoleDao;
    @Autowired
    private BootCommonService bootCommonService;
    @Autowired
    private AdminUserDao adminUserDao;
//    @Autowired
//    private AdminOrganServiceImpl adminOrganService;
	@Autowired
	private AdminRoleServiceImpl adminRoleService;
	@Autowired
	private AdminUserAuditServiceImpl adminAuditService;
	
	/****
	 * 根据用户id查找用户数据
	 * @author weishao zeng
	 * @param ids
	 * @return
	 */
    public List<AdminUser> findUserByIds(List<Long> ids) {
		return adminUserDao.findUserByIds(ids);
	}

    /***
     * 根据用户id和名称查找用户
     * @author weishao zeng
     * @param query
     * @return
     */
    public List<AdminUser> findUserByRole(FindUserByRoleQuery query) {
		return adminUserDao.findUserByRole(query);
	}
	
    public void findPage(Page<AdminUser> page, AdminUser adminUser){
        Querys.from(baseEntityManager, AdminUser.class)
        		.where()
	        		.field("id").notEqualTo(LoginUserDetails.ROOT_USER_ID)
	        		.field("userName").like(adminUser.getUserName())
	        		.field("nickName").like(adminUser.getNickName())
	        		.field("email").like(adminUser.getEmail())
	        		.field("mobile").like(adminUser.getMobile())
	        		.field("status").equalTo(adminUser.getStatus())
	        		.ignoreIfNull()
        		.end()
        		.toQuery()
        		.page(page);
    }
    
    public void save(AdminUser adminUser, MultipartFile avatarFile){
    	if(StringUtils.isBlank(adminUser.getPassword())){
    		throw new ServiceException("密码不能为空！");
    	}
    	adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
    	
    	AdminUser existUser = findByUserName(adminUser.getUserName());
    	if (existUser!=null) {
    		throw new ServiceException("用户名已被占用：" + adminUser.getUserName());
    	}
    	
    	if(avatarFile!=null){
    		FileStoredMeta meta = bootCommonService.uploadFile("web-admin", avatarFile);
    		adminUser.setAvatar(meta.getFullAccessablePath());
    	}
        Date now = new Date();
        adminUser.setCreateAt(now);
        adminUser.setUpdateAt(now);
//        if (adminUser.getOrganId()==null) {
//        	adminUser.setOrganId(0L);
//        }
        if (adminUser.getTenantId()==null) {
        	adminUser.setTenantId(0L);
        }
        baseEntityManager.save(adminUser);
    }
    
    public AdminUser loadById(Long id){
    	AdminUser user = baseEntityManager.load(AdminUser.class, id);
    	return user;
    }
    
    public AdminUser findById(Long id){
    	AdminUser user = baseEntityManager.findById(AdminUser.class, id);
    	return user;
    }
    
    public AdminUser findByUserName(String userName) {
    	return baseEntityManager.from(AdminUser.class)
    				.where()
    					.field("userName").is(userName)
    				.toQuery()
    				.unique();
    }
    
    public AdminUser lockByUserName(String userName) {
    	return baseEntityManager.from(AdminUser.class)
    				.lock(LockInfo.write())
    				.where()
    					.field("userName").is(userName)
    				.toQuery()
    				.unique();
    }
    
    public void update(UserDetail loginUser, AdminUser adminUser){
        Assert.notNull(adminUser.getId(), "参数不能为null");
        AdminUser dbAdminUser = loadById(adminUser.getId());
        if(dbAdminUser==null){
            throw new ServiceException("找不到数据：" + adminUser.getId());
        }
        
        String newPwd = adminUser.getPassword();
        //不允许修改
        adminUser.setPassword(null);
        adminUser.setId(null);
        adminUser.setUserName(null);
        ReflectUtils.copyIgnoreBlank(adminUser, dbAdminUser);
        
        Date now = new Date();
        //如果密码不为空，修改密码
    	if(StringUtils.isNotBlank(newPwd)){
    		dbAdminUser.setPassword(passwordEncoder.encode(newPwd));
//    		dbAdminUser.setLastChangePwdAt(now);
    		this.adminAuditService.saveChangePwdAudit(loginUser);
    	}
    	
        dbAdminUser.setUpdateAt(now);
        baseEntityManager.update(dbAdminUser);
    }
    
    public void deleteByIds(Long...ids){
        if(ArrayUtils.isEmpty(ids))
            throw new ServiceException("请先选择数据！");
        Stream.of(ids).forEach(id->deleteById(id));
    }
    
    public void deleteById(Long id){
        AdminUser adminUser = loadById(id);
        if(!adminRoleDao.findRolesByUser(adminUser.getId()).isEmpty()){
        	throw new ServiceException("该用户有角色关联，无法删除！请先清除用户关联的角色！");
        }
        baseEntityManager.remove(adminUser);
    }
    
    /***
     * 绑定
     * @author weishao zeng
     * @param bindingRequest
     */
//    public AdminUserBinding bindingUser(UserBindingRequest bindingRequest, boolean forceBinding) {
//    	if (bindingRequest.getBindingUserId()==null) {
//    		throw new ServiceException("绑定的用户id不能为空！");
//    	}
//    	AdminUser adminUser = this.loadById(bindingRequest.getAdminUserId());
//    	if (adminUser.isSystemRootUser()) {
//    		throw new ServiceException("终极管理员只做系统维护使用，无法绑定！");
//    	}
//    	AdminUserBinding binding = getBinding(bindingRequest.getAdminUserId());
//    	if (binding!=null) {
//    		if (forceBinding) {
//    			unbinding(binding.getId());
//    		} else {
//    			throw new ServiceException("该后台用户已绑定过其它用户").putAsMap(bindingRequest);
//    		}
//    	}
//    	
//    	binding = CopyUtils.copy(AdminUserBinding.class, bindingRequest);
//    	binding.setBindingAt(new Date());
//    	baseEntityManager.persist(binding);
//    	
//    	boolean userUpdated = false;
//    	if (StringUtils.isBlank(adminUser.getNickName())) {
//        	adminUser.setNickName(binding.getBindingUserName());
//        	userUpdated = true;
//    	}
//    	if (StringUtils.isNotBlank(bindingRequest.getAvatar())) {
//    		adminUser.setAvatar(bindingRequest.getAvatar());
//        	userUpdated = true;
//    	}
//    	if(userUpdated) {
//    		baseEntityManager.update(adminUser);
//    	}
//    	
//    	return binding;
//    }
    
//    public AdminUserBinding getBinding(Long adminUserId) {
//    	AdminUserBinding binding = this.baseEntityManager.findUnique(AdminUserBinding.class, "adminUserId", adminUserId);
//    	return binding;
//    }
//    
//    public AdminUserBinding unbinding(BindingUserId id) {
//    	AdminUserBinding binding = this.baseEntityManager.removeById(AdminUserBinding.class, id);
//    	return binding;
//    }
    
//    public UserOrganBindingVO getBindingOrgan(Long adminUserId) {
//    	AdminUser user = this.loadById(adminUserId);
//    	UserOrganBindingVO binding = null;
//    	if (user.getOrganId()!=null && user.getOrganId()>0) {
//        	binding = new UserOrganBindingVO();
//        	binding.setDataId(user.getId());
//        	binding.setBindingId(user.getOrganId());
//    		AdminOrgan organ = this.adminOrganService.load(user.getOrganId());
//    		binding.setBindingName(organ.getName());
//    	}
//    	return binding;
//    }
//    
//
//    public UserOrganBindingVO bindingOrgan(UserOrganBindingRequest bindingRequest, boolean forceBinding) {
//    	if (bindingRequest.getDataId()==null) {
//    		throw new ServiceException("绑定的用户id不能为空！");
//    	}
//    	
//    	AdminUser adminUser = this.loadById(bindingRequest.getDataId());
//    	if (adminUser.isSystemRootUser()) {
//    		throw new ServiceException("终极管理员只做系统维护使用，无法绑定！");
//    	}
//    	
//    	
//		AdminOrgan organ = this.adminOrganService.load(bindingRequest.getBindingId());
//		
//    	UserOrganBindingVO binding = getBindingOrgan(bindingRequest.getDataId());
//    	if (binding!=null && !forceBinding){
//    		throw new ServiceException("该后台用户已绑定过其它用户").putAsMap(bindingRequest);
//    	}
//
//		adminUser.setOrganId(organ.getId());
//    	this.baseEntityManager.update(adminUser);
//    	binding = new UserOrganBindingVO();
//    	binding.setDataId(adminUser.getId());
//    	binding.setBindingId(organ.getId());
//    	binding.setBindingName(organ.getName());
//    	
//    	return binding;
//    }
//
//    public void unBindingOrgan(Long adminUserId) {
//    	if (adminUserId==null) {
//    		throw new ServiceException("解绑的用户id不能为空！");
//    	}
//
//    	AdminUser adminUser = this.loadById(adminUserId);
//		adminUser.setOrganId(0L);
//    	this.baseEntityManager.update(adminUser);
//    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
	public AdminUser createOrUpdateAdminUser(CreateOrUpdateAdminUserRequest adminUser) {
		AdminUser dbUser = lockByUserName(adminUser.getUserName());
		if (dbUser==null) {
			log.info("create new admin user: {}", adminUser);
			dbUser = adminUser.asBean(AdminUser.class);
			save(dbUser, null);
			
//			UserBindingRequest binding = new UserBindingRequest();
//			binding.setAdminUserId(dbUser.getId());
//			binding.setAvatar(adminUser.getAvatar());
//			binding.setBindingUserName(dbUser.getUserName());
//			binding.setBindingUserId(adminUser.getBindingUserId());
//			bindingUser(binding, true);
		} else {
			log.info("admin user[{}] has exists!", adminUser.getUserName());
//			continue;
		}
		if (LangUtils.isNotEmpty(adminUser.getRoleIds())) {
			adminRoleService.saveUserRoles(dbUser.getId(), adminUser.getRoleIds().toArray(new Long[0]));
		}
		return dbUser;
	}
}