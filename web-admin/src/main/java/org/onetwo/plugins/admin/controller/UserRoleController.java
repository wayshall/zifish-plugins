package org.onetwo.plugins.admin.controller;

import java.util.List;

import org.onetwo.boot.core.web.view.XResponseView;
import org.onetwo.ext.permission.api.annotation.ByPermissionClass;
import org.onetwo.plugins.admin.AdminMgr.UserMgr.AssignRole;
import org.onetwo.plugins.admin.entity.AdminRole;
import org.onetwo.plugins.admin.entity.AdminUser;
import org.onetwo.plugins.admin.service.impl.AdminRoleServiceImpl;
import org.onetwo.plugins.admin.service.impl.AdminUserServiceImpl;
import org.onetwo.plugins.admin.utils.Enums.CommonStatus;
import org.onetwo.plugins.admin.utils.Enums.UserStatus;
import org.onetwo.plugins.admin.view.EasyViews.UserRoleView;
import org.onetwo.plugins.admin.vo.UserRoleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("userRole")
@Controller
public class UserRoleController extends WebAdminBaseController {
	@Autowired
	private AdminUserServiceImpl adminUserServiceImpl;
	@Autowired
	private AdminRoleServiceImpl adminRoleServiceImpl;

	@ByPermissionClass(AssignRole.class)
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @XResponseView(value="easyui", wrapper=UserRoleView.class)
    @XResponseView(value="default")
	public ModelAndView show(@PathVariable("userId") long userId) {
		AdminUser user = adminUserServiceImpl.loadById(userId);
		if(UserStatus.of(user.getStatus())!=UserStatus.NORMAL)
			return messageMv("用户非正常状态，不能分配角色！");
		
		List<AdminRole> roles = adminRoleServiceImpl.findByStatus(CommonStatus.NORMAL, user.getTenantId());
		List<Long> roleIds = adminRoleServiceImpl.findRoleIdsByUser(userId);
		
		UserRoleResponse res = UserRoleResponse.builder()
												.roles(roles)
												.userRoleIds(roleIds)
												.build();
		return responseData(res);
	}

	@ByPermissionClass(AssignRole.class)
	@RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    @XResponseView(value="default")
	public ModelAndView create(Long[] roleIds, @PathVariable("userId") long userId, RedirectAttributes redirectAttributes) {
		this.adminRoleServiceImpl.saveUserRoles(userId, roleIds);
		return messageMv("分配角色成功！");
	}

}