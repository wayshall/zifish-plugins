package org.onetwo.plugins.admin.controller;

import org.onetwo.boot.core.web.view.XResponseView;
import org.onetwo.ext.permission.api.annotation.ByPermissionClass;
import org.onetwo.plugins.admin.AdminMgr.RoleMgr;
import org.onetwo.plugins.admin.AdminMgr.UserMgr;
import org.onetwo.plugins.admin.entity.AdminOrgan;
import org.onetwo.plugins.admin.service.impl.AdminOrganServiceImpl;
import org.onetwo.plugins.admin.view.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("organ")
@XResponseView
public class AdminOrganController extends WebAdminBaseController {

    @Autowired
    private AdminOrganServiceImpl adminOrganService;
    

    @ByPermissionClass(value={UserMgr.class, RoleMgr.class}, overrideMenuUrl=false)
    @RequestMapping(method=RequestMethod.GET)
    public Object index(PageRequest pageRequest, AdminOrgan adminOrgan){
        return this.adminOrganService.findPage(pageRequest, adminOrgan);
    }
    
    @ByPermissionClass(value={UserMgr.class, RoleMgr.class}, overrideMenuUrl=false)
    @RequestMapping(value="search", method=RequestMethod.GET)
    public Object search(String query){
        return this.adminOrganService.serach(query);
    }
    
}