package org.onetwo.plugins.admin.controller;

import java.util.List;

import org.onetwo.common.data.Result;
import org.onetwo.common.spring.mvc.utils.DataResults;
import org.onetwo.common.utils.Page;
import org.onetwo.ext.permission.api.annotation.ByPermissionClass;
import org.onetwo.plugins.admin.AdminMgr.UserMgr;
import org.onetwo.plugins.admin.entity.AdminUser;
import org.onetwo.plugins.admin.service.impl.AdminUserServiceImpl;
import org.onetwo.plugins.admin.view.PageRequest;
import org.onetwo.plugins.admin.vo.UpdateAdminUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("user")
public class AdminUserController extends WebAdminBaseController {

    @Autowired
    private AdminUserServiceImpl adminUserServiceImpl;
    

    @ByPermissionClass(UserMgr.class)
    @RequestMapping(method=RequestMethod.GET)
//    @XResponseView(value="easyui", wrapper=EasyGridView.class)
    public Result index(PageRequest easyPage, AdminUser adminUser){
//        return responsePageOrData("/admin-user-index", ()->{
//        			Page<AdminUser> page = easyPage.toPageObject();//Page.create(easyPage.getPage(), easyPage.getPageSize());
//        			adminUserServiceImpl.findPage(page, adminUser);
//        			return page;
//                });
    	Page<AdminUser> page = easyPage.toPageObject();//Page.create(easyPage.getPage(), easyPage.getPageSize());
		adminUserServiceImpl.findPage(page, adminUser);
		return DataResults.success().data(page).build();
    }
    
    @ByPermissionClass(value=UserMgr.class, overrideMenuUrl=false)
    @RequestMapping(value="export", method=RequestMethod.GET)
    public ModelAndView export(PageRequest easyPage, AdminUser adminUser){
    	Page<AdminUser> page = easyPage.toPageObject();//Page.create(easyPage.getPage(), easyPage.getPageSize());
    	page.noLimited();
		adminUserServiceImpl.findPage(page, adminUser);
        return pluginMv("admin-user-export", "datas", page.getResult());
    }
    
    @ByPermissionClass(UserMgr.Create.class)
    @RequestMapping(method=RequestMethod.POST)
    public Result create(AdminUser adminUser, MultipartFile avatarFile){
        adminUserServiceImpl.save(adminUser, avatarFile);
        return DataResults.success("保存成功！").build();
    }
    
    @ByPermissionClass(UserMgr.class)
    @RequestMapping(value="{id}", method=RequestMethod.GET)
    public Result show(@PathVariable("id") Long id){
        AdminUser adminUser = adminUserServiceImpl.loadById(id);
        adminUser.setPassword("");
        return DataResults.success().data(adminUser).build();
    }
    
    @ByPermissionClass(UserMgr.Update.class)
    @RequestMapping(value="{id}", method=RequestMethod.PUT)
    public Result update(@PathVariable("id") Long id, UpdateAdminUserRequest updateAdminUserRequest){
        adminUserServiceImpl.update(id, updateAdminUserRequest);
        return DataResults.success("更新成功！").build();
    }
    
    
    @ByPermissionClass(UserMgr.Delete.class)
    @RequestMapping(method=RequestMethod.DELETE)
    public Result deleteBatch(Long[] ids){
        adminUserServiceImpl.deleteByIds(ids);
        return DataResults.success("删除成功！").build();
    }
    

    @ByPermissionClass
    @GetMapping("/findListByField")
    public Result findListByField(String field, String[] values){
        List<AdminUser> list = adminUserServiceImpl.findListByField(field, values);
        return DataResults.data(list).build();
    }
}