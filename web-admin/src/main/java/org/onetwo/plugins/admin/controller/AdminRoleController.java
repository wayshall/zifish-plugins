

package org.onetwo.plugins.admin.controller;

import org.onetwo.common.data.Result;
import org.onetwo.common.spring.mvc.utils.DataResults;
import org.onetwo.common.utils.Page;
import org.onetwo.ext.permission.api.annotation.ByPermissionClass;
import org.onetwo.plugins.admin.AdminMgr.RoleMgr;
import org.onetwo.plugins.admin.entity.AdminRole;
import org.onetwo.plugins.admin.service.impl.AdminRoleServiceImpl;
import org.onetwo.plugins.admin.view.EasyDataGrid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("role")
public class AdminRoleController extends WebAdminBaseController {

    @Autowired
    private AdminRoleServiceImpl adminRoleServiceImpl;
    
    
    @ByPermissionClass(RoleMgr.class)
    @RequestMapping(method=RequestMethod.GET)
//    @XResponseView(value="easyui", wrapper=EasyGridView.class)
    public Page<AdminRole> index(EasyDataGrid<AdminRole> easyPage, AdminRole adminRole){
    	Page<AdminRole> page = Page.create(easyPage.getPage(), easyPage.getPageSize());
        adminRoleServiceImpl.findPage(page, adminRole);
        return page;
    }
    
    @ByPermissionClass(RoleMgr.Create.class)
    @RequestMapping(method=RequestMethod.POST)
    public Result create(AdminRole adminRole){
        adminRoleServiceImpl.save(adminRole);
        return DataResults.success("保存成功！").build();
    }
    @ByPermissionClass(RoleMgr.class)
    @RequestMapping(value="{id}", method=RequestMethod.GET)
    public AdminRole show(@PathVariable("id") Long id){
        AdminRole adminRole = adminRoleServiceImpl.loadById(id);
        return adminRole;
    }
    
    @ByPermissionClass(RoleMgr.Update.class)
    @RequestMapping(value="{id}", method=RequestMethod.PUT)
    public Result update(@PathVariable("id") Long id, AdminRole adminRole){
        adminRole.setId(id);
        adminRoleServiceImpl.update(adminRole);
        return DataResults.success("更新成功！").build();
    }
    
    
    @ByPermissionClass(RoleMgr.Delete.class)
    @RequestMapping(method=RequestMethod.DELETE)
    public Result deleteBatch(Long[] ids){
        adminRoleServiceImpl.deleteByIds(ids);
        return DataResults.success("删除成功！").build();
    }
}