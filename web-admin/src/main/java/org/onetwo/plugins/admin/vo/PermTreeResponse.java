package org.onetwo.plugins.admin.vo;
/**
 * @author weishao zeng
 * <br/>
 */

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class PermTreeResponse {

	List<PermTreeModel> treeList;
	List<Map<String, String>> permissionTypes;
	List<Map<String, String>> dataFroms;
}

