package org.onetwo.plugins.admin.vo;

import java.util.List;

import org.onetwo.common.tree.DefaultTreeModel;
import org.onetwo.plugins.admin.entity.AdminPermission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionReponse {
	List<String> rolePerms;
	List<AdminPermission> allPerms;
	List<DefaultTreeModel> treePerms;
}

