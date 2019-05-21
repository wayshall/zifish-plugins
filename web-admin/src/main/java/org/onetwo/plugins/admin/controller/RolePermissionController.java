package org.onetwo.plugins.admin.controller;


import javax.annotation.Resource;

import org.onetwo.boot.core.web.view.XResponseView;
import org.onetwo.ext.permission.api.annotation.ByPermissionClass;
import org.onetwo.plugins.admin.AdminMgr.RoleMgr.AssignPermission;
import org.onetwo.plugins.admin.service.impl.AdminRoleServiceImpl;
import org.onetwo.plugins.admin.view.EasyViews.RolePermissionView;
import org.onetwo.plugins.admin.view.RolePermissionTreeView;
import org.onetwo.plugins.admin.vo.RolePermissionReponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("rolePermission")
@Controller
public class RolePermissionController extends WebAdminBaseController {
	@Resource
	private AdminRoleServiceImpl adminRoleServiceImpl;
	

	@ByPermissionClass(AssignPermission.class)
	@RequestMapping(value="{roleId}", method=RequestMethod.GET)
	@XResponseView(value="easyui", wrapper=RolePermissionView.class)
	@XResponseView(value=XResponseView.DEFAULT_VIEW, wrapper=RolePermissionTreeView.class)
//	public ModelAndView show(String appCode, @PathVariable("roleId") long roleId){
	public ModelAndView show(@PathVariable("roleId") long roleId){
		/*List<String> rolePerms = this.adminRoleServiceImpl.findAppPermissionCodesByRoleIds(appCode, roleId);
		List<AdminPermission> allPerms = adminRoleServiceImpl.findAppPermissions(appCode);
		
		RolePermissionReponse res = RolePermissionReponse.builder()
								.rolePerms(rolePerms)
								.allPerms(allPerms)
								.build();*/
		RolePermissionReponse res = this.adminRoleServiceImpl.findRolePermissionsByRoleId(roleId);
		return responseData(res);
	}
	

	@ByPermissionClass(AssignPermission.class)
	@RequestMapping(value="{roleId}", method=RequestMethod.POST)
	public ModelAndView create(@PathVariable("roleId") long roleId, String[] permissionCodes){
//	public ModelAndView create(String appCode, @PathVariable("roleId") long roleId, String[] permissionCodes){
		String msg = "保存权限成功！";
		this.adminRoleServiceImpl.saveRolePermission(roleId, permissionCodes);
		return messageMv(msg);
	}
	
	
}