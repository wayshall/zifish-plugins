package org.onetwo.plugins.admin.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.onetwo.boot.core.web.view.XResponseView;
import org.onetwo.common.data.Result;
import org.onetwo.common.exception.ServiceException;
import org.onetwo.common.spring.mvc.utils.DataResults;
import org.onetwo.common.tree.DefaultTreeModel;
import org.onetwo.common.tree.TreeBuilder;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.ext.permission.api.DataFrom;
import org.onetwo.ext.permission.api.IPermission;
import org.onetwo.ext.permission.api.PermissionType;
import org.onetwo.ext.permission.api.annotation.ByPermissionClass;
import org.onetwo.ext.permission.entity.PermisstionTreeModel;
import org.onetwo.ext.permission.service.MenuItemRepository;
import org.onetwo.ext.permission.utils.PermissionUtils;
import org.onetwo.plugins.admin.AdminMgr.PermMgr;
import org.onetwo.plugins.admin.entity.AdminPermission;
import org.onetwo.plugins.admin.service.impl.PermissionManagerImpl;
import org.onetwo.plugins.admin.view.RolePermissionTreeView;
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
	@RequestMapping(method=RequestMethod.GET)
	public PermTreeResponse tree(UserDetail userDetail){
		List<DefaultTreeModel> menus = menuItemRepository.findUserPermissions(userDetail, (userPerms, allPerms)->{
			Function<IPermission, DefaultTreeModel> treeModelCreater = RolePermissionTreeView.TREE_MODEL_CREATER;
			TreeBuilder<DefaultTreeModel> treebuilder = PermissionUtils.createMenuTreeBuilder(userPerms, treeModelCreater);
			treebuilder.buidTree(node->{
				if (node.getParentId()==null) {
					return null;
				}
				AdminPermission p = (AdminPermission)allPerms.get(node.getParentId());
				if (p==null) {
					throw new ServiceException("找不到节点[" + node.getId() + "]的父节点：" + node.getParentId());
				}
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
		
		List<Map<String, String>> dataFroms = Stream.of(DataFrom.values()).map(p -> {
			Map<String, String> map = new HashMap<>();
			map.put("label", p.getLabel());
			map.put("value", p.getValue());
			return map;
		})
		.collect(Collectors.toList());
		res.setDataFroms(dataFroms);
		
		return res;
	}
	
	@ByPermissionClass(PermMgr.class)
	@RequestMapping(value="", method=RequestMethod.POST)
	public Result create(@Valid AdminPermission perm){
		if (perm.getDataFrom()==null) {
			perm.setDataFrom(DataFrom.MANUAL);
		}
		permissionManager.persist(perm);
		return DataResults.success("添加成功！").build();
	}
	
	@ByPermissionClass(PermMgr.class)
	@RequestMapping(value="/{code}", method=RequestMethod.GET)
	public AdminPermission detail(@PathVariable("code") String code){
		AdminPermission perm = permissionManager.findByCode(code);
		return perm;
	}
	
	@ByPermissionClass(PermMgr.class)
	@RequestMapping(value="/{code}", method=RequestMethod.PUT)
	public Result update(@PathVariable("code") String code, @Valid AdminPermission perm){
		// 标记为手动修改过
		if (perm.getDataFrom()==null) {
			perm.setDataFrom(DataFrom.MANUAL);
		}
		perm.setCode(code);
		permissionManager.update(perm);
		return DataResults.success("更新成功！").build();
	}

	@ByPermissionClass(PermMgr.class)
	@RequestMapping(value="/{code}", method=RequestMethod.DELETE)
	public Result delete(@PathVariable("code") String code){
		permissionManager.delete(code);
		return DataResults.success("删除成功！").build();
	}

	@ByPermissionClass(PermMgr.Refresh.class)
	@RequestMapping(value="/refresh", method=RequestMethod.PUT)
	public Result refresh(){
		this.permissionManager.refreshSecurityMetadataSource();
		return DataResults.success("刷新权限缓存成功！").build();
	}
	
}