package org.onetwo.plugins.admin.vo;

import org.onetwo.common.tree.AbstractTreeModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties({"parent", "level", "index", "first", "last"})
public class PermTreeModel extends AbstractTreeModel<PermTreeModel> {

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

