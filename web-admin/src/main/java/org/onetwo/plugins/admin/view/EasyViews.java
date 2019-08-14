package org.onetwo.plugins.admin.view;

import java.util.List;

import org.onetwo.common.data.DataResultWrapper;
import org.onetwo.common.utils.Page;
import org.onetwo.common.utils.map.MappableMap;
import org.onetwo.plugins.admin.entity.AdminPermission;
import org.onetwo.plugins.admin.entity.AdminRole;
import org.onetwo.plugins.admin.vo.RolePermissionReponse;
import org.onetwo.plugins.admin.vo.UserRoleResponse;

/**
 * @author wayshall
 * <br/>
 */
public class EasyViews {
	
	public static class EasyGridView implements DataResultWrapper {

		@Override
		public Object wrapResult(Object data) {
			Object newData = data;
			if(data instanceof Page){
				newData = EasyDataGrid.create((Page<?>)data);
			}else if(data instanceof List){
				newData = EasyDataGrid.create((List<?>)data);
			}
			return newData;
		}
		
	}
	
	public static class UserRoleView implements DataResultWrapper {

		@Override
		public Object wrapResult(Object data) {
			UserRoleResponse res = (UserRoleResponse) data;
			List<MappableMap> rolelist = EasyModel.newComboBoxBuilder(AdminRole.class)
					 .specifyMappedFields()
					 .mapText("name")
					 .mapValue("id")
					 .mapSelected(role->res.getUserRoleIds().contains(role.getId()))
					 .build(res.getRoles());
			return rolelist;
		}
		
	}
	
	public static class RolePermissionView implements DataResultWrapper {

		@Override
		public Object wrapResult(Object responseData) {
			RolePermissionReponse reponse = (RolePermissionReponse) responseData;
			EasyChildrenTreeModel treeModel = EasyModel.newChildrenTreeBuilder(AdminPermission.class)
					 .mapId("code")
					 .mapText("name")
					 .mapParentId("parentCode")
					 .mapChecked(src->{
						 return reponse.getRolePerms().contains(src.getCode());
					 })
					 .mapIsStateOpen(src->true)
					 .build(reponse.getAllPerms(), null);
			return treeModel;
		}
		
	}

}
