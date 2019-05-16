package org.onetwo.plugins.admin.controller;

import javax.validation.Valid;

import org.onetwo.common.data.Result;
import org.onetwo.common.spring.mvc.utils.DataResults;
import org.onetwo.ext.permission.api.annotation.ByPermissionClass;
import org.onetwo.plugins.admin.AdminMgr.UserMgr.Binding;
import org.onetwo.plugins.admin.entity.AdminUserBinding;
import org.onetwo.plugins.admin.entity.AdminUserBinding.BindingUserId;
import org.onetwo.plugins.admin.service.impl.AdminUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("userBinding")
@RestController
public class UserBindingController extends WebAdminBaseController {
	@Autowired
	private AdminUserServiceImpl adminUserService;

	@ByPermissionClass(Binding.class)
	@GetMapping(path="{adminUserId}")
	public Result getBinding(@PathVariable("adminUserId") Long adminUserId) {
		AdminUserBinding binding = adminUserService.getBinding(adminUserId);
		return DataResults.success().data(binding).build();
	}

	@ByPermissionClass(Binding.class)
	@PostMapping(path="")
	public Result binding(@Valid AdminUserBinding id) {
		adminUserService.bindingUser(id, true);
		return DataResults.success("绑定成功！").build();
	}
	
	@ByPermissionClass(Binding.class)
	@DeleteMapping(path="")
	public Result unbinding(@Valid BindingUserId id) {
		adminUserService.unbinding(id);
		return DataResults.success("解绑成功！").build();
	}


}