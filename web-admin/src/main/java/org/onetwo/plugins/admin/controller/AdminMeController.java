package org.onetwo.plugins.admin.controller;

import java.util.List;

import org.onetwo.common.spring.copier.CopyUtils;
import org.onetwo.common.web.userdetails.UserDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@RestController
public class AdminMeController extends WebAdminBaseController {
	
	@GetMapping("me")
	public AdminUserInfo me(){
		UserDetail userDetail = this.checkAndGetCurrentLoginUser();
		AdminUserInfo user = CopyUtils.copyFrom(userDetail)
										.propertyMapping("nickName", "nickname")
				 						.toClass(AdminUserInfo.class);
		return user;
	}
	
	@Data
	public static class AdminUserInfo {
		Long userId;
		String nickName;
		String userName;
		String avatar;
		List<String> roles;
	}

}
