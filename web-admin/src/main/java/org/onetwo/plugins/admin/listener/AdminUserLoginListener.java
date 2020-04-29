package org.onetwo.plugins.admin.listener;

import java.lang.reflect.Method;
import java.util.List;

import org.onetwo.boot.core.web.mvc.log.OperatorLogEvent;
import org.onetwo.boot.core.web.mvc.log.OperatorLogInfo;
import org.onetwo.common.log.JFishLoggerFactory;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.ext.permission.PermissionManager;
import org.onetwo.ext.permission.api.IPermission;
import org.onetwo.plugins.admin.annotation.UserLog;
import org.onetwo.plugins.admin.entity.AdminUserLogEntity;
import org.onetwo.plugins.admin.service.impl.AdminUserLogServiceImpl;
import org.onetwo.plugins.admin.utils.WebAdminProperties;
import org.onetwo.plugins.admin.utils.WebAdminProperties.UserLogProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author weishao zeng
 * <br/>
 */
@Service
public class AdminUserLoginListener {
	@Autowired
	private AdminUserLogServiceImpl adminLoginLogService;
	@Autowired
	private PermissionManager<? extends IPermission> permissionManager;
	@Autowired
	private WebAdminProperties webAdminProperties;

	@EventListener
	@Async
	public void onLogEvent(OperatorLogEvent event){
		UserLogProps userlogProps = webAdminProperties.getUserlog();
		JFishLoggerFactory.getCommonLogger().info("log AdminUserLoginListener");
		
		OperatorLogInfo operatorLog = event.getOperatorLog();
		AdminUserLogEntity userLog = AdminUserLogServiceImpl.buildLog(operatorLog);
		
		UserLog userLogAnno = operatorLog.getHandlerMethod()!=null?operatorLog.getHandlerMethod().getMethodAnnotation(UserLog.class):null;
		if (userLogAnno!=null) {
			userLog.setOperationName(userLogAnno.value());
			userLog.setOperationCode(userLogAnno.operationCode());
			adminLoginLogService.save(userLog);
			
		} else if (userlogProps.isLogByPermission()) {
			if (userlogProps.isLogRequestMethod(operatorLog.getRequestMethod())) {
				logMethod(operatorLog.getHandlerMethod().getMethod(), userLog);
			}
		}
	}
	
	private void logMethod(Method method, AdminUserLogEntity userLog) {
		List<IPermission> perms = (List<IPermission>)permissionManager.getMethodPermissionMapping().get(method);
//		userLog.setOperationCode(operationCode);
		if (LangUtils.isNotEmpty(perms)) {
			IPermission perm = perms.get(0);
			userLog.setOperationName(perm.getName());
			userLog.setOperationCode(perm.getCode());
			adminLoginLogService.save(userLog);
		}
	}
	
}
