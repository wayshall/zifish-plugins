package org.onetwo.plugins.admin.listener;

import org.onetwo.common.log.JFishLoggerFactory;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.plugins.admin.entity.AdminLoginLogEntity;
import org.onetwo.plugins.admin.service.impl.AdminLoginLogServiceImpl;
import org.onetwo.plugins.admin.service.impl.AdminUserAuditServiceImpl;
import org.onetwo.plugins.admin.utils.AdminOperationCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * @author weishao zeng
 * <br/>
 */
@Service
public class LoginSuccessListener {
	
	@Autowired
	private AdminLoginLogServiceImpl adminLoginLogService;
	@Autowired
	private AdminUserAuditServiceImpl adminAuditService;
    
	@EventListener
	public void onAuthenticationSuccess(InteractiveAuthenticationSuccessEvent event){
		try {
			AdminLoginLogEntity log = buildLog(event.getAuthentication());
			log.setIsSuccess(true);
			adminLoginLogService.save(log);
			if (log.getUserId()!=null && log.getUserId()>0) {
				adminAuditService.saveUserLoginAudit(log);
			}
		} catch (Exception e) {
			JFishLoggerFactory.getCommonLogger().warn("记录登录日志出错：" + e.getMessage());
		}
	}

	public static AdminLoginLogEntity buildLog(Authentication authentication) {
		AdminLoginLogEntity log = null;
		Object details = authentication.getPrincipal();
		if (details instanceof UserDetail) {
			UserDetail userDetail = (UserDetail) details;
			log = AdminLoginLogServiceImpl.buildLog(AdminOperationCodes.LOGIN, userDetail);
		} else {
			log = AdminLoginLogServiceImpl.buildLog(AdminOperationCodes.LOGIN, null);
		}
		return log;
	}
}
