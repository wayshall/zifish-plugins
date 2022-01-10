package org.onetwo.plugins.admin.controller;

import org.onetwo.boot.core.web.controller.AbstractBaseController;
import org.onetwo.common.spring.validator.ValidatorUtils;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.ext.permission.api.annotation.ByPermissionClass;
import org.onetwo.plugins.admin.AdminMgr.UserProfile;
import org.onetwo.plugins.admin.entity.AdminUser;
import org.onetwo.plugins.admin.service.impl.AdminUserServiceImpl;
import org.onetwo.plugins.admin.utils.WebConstant.ValidGroup.ValidAnyTime;
import org.onetwo.plugins.admin.utils.WebConstant.ValidGroup.ValidWhenEdit;
import org.onetwo.plugins.admin.vo.AdminLoginUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/userProfile")
public class UserProfileController extends AbstractBaseController {

    @Autowired
    private AdminUserServiceImpl adminUserServiceImpl;
    

    @ByPermissionClass(UserProfile.class)
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView showProfile(UserDetail loginUser){
        return responsePageOrData("user-profile", ()->{
        	AdminUser adminUser = adminUserServiceImpl.loadById(loginUser.getUserId());
            adminUser.setPassword("");
            adminUser.setCreateAt(null);
            adminUser.setUpdateAt(null);
            return adminUser;
        });
    }
    
    @ByPermissionClass(UserProfile.class)
    @RequestMapping(method=RequestMethod.PUT)
    public ModelAndView update(@Validated({ValidAnyTime.class, ValidWhenEdit.class}) AdminUser adminUser, BindingResult br){
    	ValidatorUtils.throwIfHasErrors(br, true);
    	UserDetail loginUser = checkAndGetCurrentLoginUser(AdminLoginUserInfo.class, true);
        adminUser.setId(loginUser.getUserId());
        adminUser.setUserName(loginUser.getUserName());
        adminUserServiceImpl.update(loginUser, adminUser);
        return messageMv("更新成功！");
    }
    
}