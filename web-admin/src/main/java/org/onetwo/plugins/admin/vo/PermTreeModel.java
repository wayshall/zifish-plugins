package org.onetwo.plugins.admin.vo;

import java.util.function.Function;

import org.onetwo.common.tree.AbstractTreeModel;
import org.onetwo.ext.permission.api.IPermission;
import org.onetwo.plugins.admin.entity.AdminPermission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties({"parent", "level", "index", "first", "last"})
public class PermTreeModel extends AbstractTreeModel<PermTreeModel> {
	
	public static Function<IPermission, PermTreeModel> TREE_MODEL_CREATER = perm->{
		AdminPermission adminPerm = (AdminPermission) perm;
		PermTreeModel tm = new PermTreeModel(perm.getCode(), perm.getName(), perm.getParentCode());
		tm.setSort(adminPerm.getSort());
		return tm;
	};

	public PermTreeModel(Object id, String name, Object parentId, Comparable<Object> sort) {
		super(id, name, parentId, sort);
	}

	public PermTreeModel(Object id, String name, Object parentId) {
		super(id, name, parentId);
	}
	
	public String getLabel() {
		return getName();
	}

}

