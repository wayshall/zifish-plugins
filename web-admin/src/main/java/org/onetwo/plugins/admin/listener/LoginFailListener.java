package org.onetwo.plugins.admin.listener;

import org.onetwo.plugins.admin.entity.AdminLoginLogEntity;
import org.onetwo.plugins.admin.service.impl.AdminLoginLogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Service;

/**
 * @author weishao zeng
 * <br/>
 */
@Service
public class LoginFailListener {
	
	@Autowired
	private AdminLoginLogServiceImpl adminLoginLogService;
	
	@EventListener
	public void onBadCredentials(AuthenticationFailureBadCredentialsEvent event){
		AdminLoginLogEntity log = LoginSuccessListener.buildLog(event.getAuthentication());
		log.setIsSuccess(false);
		log.setErrorMsg(event.getException().getMessage());
		adminLoginLogService.save(log);
	}
	
	
}
