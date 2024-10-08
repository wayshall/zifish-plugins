package org.onetwo.plugins.admin;

import org.onetwo.ext.permission.api.PermissionType;

public interface AdminMgr {
	String name = "系统管理";
	String appCode = AdminMgr.class.getSimpleName();
	int sort = 100_000_000;

	public interface UserProfile {
		String name = "修改资料";
	}

	public static interface ApplicationMgr {
		String name = "应用管理";
		int sort = 3;

		public static interface Create {
			String name = "新增";
			PermissionType permissionType = PermissionType.FUNCTION;
		}

		public static interface Update {
			String name = "更新";
			PermissionType permissionType = PermissionType.FUNCTION;
		}

		public static interface Delete {
			String name = "删除";
			PermissionType permissionType = PermissionType.FUNCTION;
		}
	}
	
	public static interface PermMgr {
		String name = "权限管理";
		int sort = 4;
		
		public interface Refresh {
			String name = "刷新权限";
			PermissionType permissionType = PermissionType.FUNCTION;
		}
	}
	
	public static interface RoleMgr {
		String name = "角色管理";
		int sort = 5;
/*		public static interface List {
			String name = "角色列表";
			int sort =1;
		}
*/
		public static interface Create {
			String name = "新增";
			PermissionType permissionType = PermissionType.FUNCTION;
		}

		public static interface Update {
			String name = "更新";
			PermissionType permissionType = PermissionType.FUNCTION;
		}

		public static interface Delete {
			String name = "删除";
			PermissionType permissionType = PermissionType.FUNCTION;
		}
		
		public static interface AssignPermission {
			String name = "分配权限";
			PermissionType permissionType = PermissionType.FUNCTION;
		}
	}

	public static interface UserMgr {
		String name = "用户管理";
		int sort = 10;
		/*public static interface List {
			String name = "用户列表";
			int sort =1;
		}*/

		public static interface Create {
			String name = "新增";
			PermissionType permissionType = PermissionType.FUNCTION;
		}

		public static interface Update {
			String name = "更新";
			PermissionType permissionType = PermissionType.FUNCTION;
		}
		
		public static interface Delete {
			String name = "删除";
			PermissionType permissionType = PermissionType.FUNCTION;
		}

		public static interface AssignRole {
			String name = "分配角色";
			PermissionType permissionType = PermissionType.FUNCTION;
		}

		public static interface Binding {
			String name = "用户绑定";
			PermissionType permissionType = PermissionType.FUNCTION;
		}
	}
	

	public interface DictMgr {
		String name = "字典管理";
	}
}
