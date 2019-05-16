package org.onetwo.plugins.admin.view;

import java.util.List;
import java.util.function.Function;

import org.onetwo.boot.core.web.view.DefaultDataResultWrapper;
import org.onetwo.common.data.DataResultWrapper;
import org.onetwo.common.tree.TreeBuilder;
import org.onetwo.ext.permission.api.IPermission;
import org.onetwo.ext.permission.utils.PermissionUtils;
import org.onetwo.plugins.admin.vo.PermTreeModel;
import org.onetwo.plugins.admin.vo.RolePermissionReponse;

/**
 * @author weishao zeng
 * <br/>
 */
public class RolePermissionTreeView extends DefaultDataResultWrapper implements DataResultWrapper {

	@Override
	public Object wrapResult(Object responseData) {
		RolePermissionReponse response = (RolePermissionReponse) responseData;
		Function<IPermission, PermTreeModel> treeModelCreater = PermTreeModel.TREE_MODEL_CREATER;
		TreeBuilder<PermTreeModel> treebuilder = PermissionUtils.createMenuTreeBuilder(response.getAllPerms(), treeModelCreater);
		List<PermTreeModel> tree = treebuilder.buidTree();
		response.setTreePerms(tree);
		response.setAllPerms(null);
		return wrapAsDataResultIfNeed(response);
	}

}

