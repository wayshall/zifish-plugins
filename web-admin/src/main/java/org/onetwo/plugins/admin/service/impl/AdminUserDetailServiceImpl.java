package org.onetwo.plugins.admin.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.reflect.ReflectUtils;
import org.onetwo.ext.permission.utils.PermissionUtils;
import org.onetwo.plugins.admin.dao.AdminPermissionDao;
import org.onetwo.plugins.admin.entity.AdminOrgan;
import org.onetwo.plugins.admin.entity.AdminPermission;
import org.onetwo.plugins.admin.entity.AdminUser;
import org.onetwo.plugins.admin.entity.AdminUserBinding;
import org.onetwo.plugins.admin.utils.Enums.UserStatus;
import org.onetwo.plugins.admin.vo.AdminLoginUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unchecked")
@Transactional(readOnly=true)
public class AdminUserDetailServiceImpl<T extends AdminUser> implements UserDetailsService {

    @Autowired
    protected BaseEntityManager baseEntityManager;
	@Autowired
	protected AdminPermissionDao adminPermissionDao;
	@Autowired
	private PermissionManagerImpl permissionManager;
	@Autowired
	private AdminOrganServiceImpl adminOrganService;
	@Autowired
	private AdminUserServiceImpl adminUserService;
	
	protected Class<T> userDetailClass;

	public AdminUserDetailServiceImpl() {
		super();
		this.userDetailClass = (Class<T>)ReflectUtils.getSuperClassGenricType(this.getClass());
	}

	public AdminUserDetailServiceImpl(Class<T> userDetailClass) {
		super();
		this.userDetailClass = userDetailClass;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		T user = fetUserByUserName(username);
		if(!UserStatus.NORMAL.name().equals(user.getStatus())){
			throw new LockedException("用户状态异常："+user.getStatusName());
		}
		
		List<GrantedAuthority> authes = fetchUserGrantedAuthorities(user);
		UserDetails userDetail = buildUserDetail(user, authes);
		return userDetail;
	}
	
	protected List<GrantedAuthority> fetchUserGrantedAuthorities(T user){
		List<GrantedAuthority> authes = Collections.emptyList();
		if(user.isSystemRootUser()){
			List<AdminPermission> perms = adminPermissionDao.findAppPermissions(null);
			authes = perms.stream().map(perm->new SimpleGrantedAuthority(perm.getCode()))
						.collect(Collectors.toList());
		}else{
			List<AdminPermission> perms = this.adminPermissionDao.findAppPermissionsByUserId(null, user.getId());
			
			// 若分配权限的时候，半选中的父节点没有保存（保存父节点会导致前端回显的时候，因为父节点选中而导致未选择的子节点也会选中问题），所以这里通过构建树的方式把版选中的父菜单也查找出来
			// 若分配权限时已保存半选中的父节点，则不需要下面的逻辑
			PermissionUtils.createMenuTreeBuilder(perms).buidTree(node -> {
				if (node.getParentId()==null) {
					return null;//node;
				}
				AdminPermission p = permissionManager.findByCode((String)node.getParentId());
				if (p!=null) {
					perms.add(p);
				} 
				return null;
			});
			
			authes = perms.stream().map(perm->new SimpleGrantedAuthority(perm.getCode()))
						.collect(Collectors.toList());
		}
		return authes;
	}
	
	protected T fetUserByUserName(String username){
		List<T> users = baseEntityManager.findList(userDetailClass, "userName", username);
		T user = users.stream().findFirst().orElseThrow( ()-> new UsernameNotFoundException(username));
		return user;
	}
	
	protected UserDetails buildUserDetail(T user, List<GrantedAuthority> authes){
		AdminLoginUserInfo userDetail = new AdminLoginUserInfo(user.getId(), user.getUserName(), user.getPassword(), authes);
		userDetail.setNickname(user.getNickName());
		userDetail.setAvatar(user.getAvatar());
		if (user.getOrganId()!=null && user.getOrganId()>0) {
			AdminOrgan organ = this.adminOrganService.load(user.getOrganId());
			userDetail.setOrganId(organ.getId());
			userDetail.setTenantId(organ.getId());
		}

        AdminUserBinding binding = adminUserService.getBinding(user.getId());
        if (binding!=null) {
        	userDetail.setBindingUserId(binding.getBindingUserId());
        }
        
		return userDetail;
	}
	
}
