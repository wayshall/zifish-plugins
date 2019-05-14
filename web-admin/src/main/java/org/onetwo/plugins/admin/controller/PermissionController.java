package org.onetwo.plugins.admin.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.onetwo.boot.core.web.view.XResponseView;
import org.onetwo.common.tree.TreeBuilder;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.ext.permission.api.IPermission;
import org.onetwo.ext.permission.api.PermissionType;
import org.onetwo.ext.permission.api.annotation.ByPermissionClass;
import org.onetwo.ext.permission.entity.PermisstionTreeModel;
import org.onetwo.ext.permission.service.MenuItemRepository;
import org.onetwo.ext.permission.utils.PermissionUtils;
import org.onetwo.plugins.admin.AdminMgr.PermMgr;
import org.onetwo.plugins.admin.entity.AdminPermission;
import org.onetwo.plugins.admin.service.impl.PermissionManagerImpl;
import org.onetwo.plugins.admin.vo.PermTreeModel;
import org.onetwo.plugins.admin.vo.PermTreeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("permission")
@RestController
@XResponseView
public class PermissionController extends WebAdminBaseController {
	@Resource
	private MenuItemRepository<PermisstionTreeModel> menuItemRepository;
	@Autowired
	private PermissionManagerImpl permissionManager;

	@ByPermissionClass(PermMgr.class)
	@RequestMapping(value="tree", method=RequestMethod.GET)
	public PermTreeResponse tree(UserDetail userDetail){
		List<PermTreeModel> menus = menuItemRepository.findUserPermissions(userDetail, (userPerms, allPerms)->{
			Function<IPermission, PermTreeModel> treeModelCreater = perm->{
				AdminPermission adminPerm = (AdminPermission) perm;
				PermTreeModel tm = new PermTreeModel(perm.getCode(), perm.getName(), perm.getParentCode());
//				tm.setHidden(perm.getPermissionType()==PermissionType.FUNCTION);
//				tm.addMetas(adminPerm.getMeta());
				tm.setSort(adminPerm.getSort());
				return tm;
			};
			TreeBuilder<PermTreeModel> treebuilder = PermissionUtils.createMenuTreeBuilder(userPerms, treeModelCreater);
			treebuilder.buidTree(node->{
				AdminPermission p = (AdminPermission)allPerms.get(node.getParentId());
				return treeModelCreater.apply(p);
			});
			return treebuilder.getRootNodes();
		});
		
		PermTreeResponse res = new PermTreeResponse();
		res.setTreeList(menus);
		List<Map<String, String>> types = Stream.of(PermissionType.values()).map(p -> {
			Map<String, String> map = new HashMap<>();
			map.put("label", p.getLabel());
			map.put("value", p.getValue());
			return map;
		})
		.collect(Collectors.toList());
		res.setPermissionTypes(types);
		return res;
	}
	
	@ByPermissionClass(PermMgr.class)
	@RequestMapping(value="/detail/{code}", method=RequestMethod.GET)
	public AdminPermission detail(@PathVariable("code") String code){
		AdminPermission perm = permissionManager.findByCode(code);
		return perm;
	}
	
	
}