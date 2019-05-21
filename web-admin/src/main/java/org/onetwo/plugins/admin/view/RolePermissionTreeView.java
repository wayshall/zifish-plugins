package org.onetwo.plugins.admin.view;

import java.util.List;
import java.util.function.Function;

import org.onetwo.boot.core.web.view.DefaultDataResultWrapper;
import org.onetwo.common.data.DataResultWrapper;
import org.onetwo.common.tree.DefaultTreeModel;
import org.onetwo.common.tree.TreeBuilder;
import org.onetwo.ext.permission.api.IPermission;
import org.onetwo.ext.permission.utils.PermissionUtils;
import org.onetwo.plugins.admin.entity.AdminPermission;
import org.onetwo.plugins.admin.vo.RolePermissionReponse;

/**
 * @author weishao zeng
 * <br/>
 */
public class RolePermissionTreeView extends DefaultDataResultWrapper implements DataResultWrapper {
	public static Function<IPermission, DefaultTreeModel> TREE_MODEL_CREATER = perm->{
		AdminPermission adminPerm = (AdminPermission) perm;
		DefaultTreeModel tm = new DefaultTreeModel(perm.getCode(), perm.getName(), perm.getParentCode());
		tm.setSort(adminPerm.getSort());
		return tm;
	};

	@Override
	public Object wrapResult(Object responseData) {
		RolePermissionReponse response = (RolePermissionReponse) responseData;
		Function<IPermission, DefaultTreeModel> treeModelCreater = TREE_MODEL_CREATER;
		TreeBuilder<DefaultTreeModel> treebuilder = PermissionUtils.createMenuTreeBuilder(response.getAllPerms(), treeModelCreater);
		List<DefaultTreeModel> tree = treebuilder.buidTree();
		response.setTreePerms(tree);
		response.setAllPerms(null);
		return wrapAsDataResultIfNeed(response);
	}

}

