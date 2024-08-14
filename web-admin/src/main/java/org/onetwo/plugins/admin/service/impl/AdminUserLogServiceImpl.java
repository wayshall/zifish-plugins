
package org.onetwo.plugins.admin.service.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.onetwo.boot.core.web.mvc.log.OperatorLogInfo;
import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.utils.Page;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.common.web.utils.RequestUtils;
import org.onetwo.common.web.utils.WebHolder;
import org.onetwo.plugins.admin.entity.AdminUserLogEntity;
import org.onetwo.plugins.admin.utils.AdmnOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminUserLogServiceImpl {
	
	@Autowired
	private BaseEntityManager baseEntityManager;
	
    @Autowired
    public AdminUserLogServiceImpl() {
    }
    
    @Transactional(readOnly=true)
    public Page<AdminUserLogEntity> findPage(Page<AdminUserLogEntity> page, AdminUserLogEntity example) {
        return baseEntityManager.from(AdminUserLogEntity.class)
                                .where()
                                    .addFields(example)
                                    .ignoreIfNull()
                                .end()
                                .toQuery()
                                .page(page);
    }

    @Transactional
    public AdminUserLogEntity save(AdminUserLogEntity log) {
    	return baseEntityManager.save(log);
    }
    
    /****
     * 记录用户操作日志
     * @author weishao zeng
     * @param userDetail
     * @param admnOperation
     * @return
     */
    @Transactional
    public AdminUserLogEntity logUserOperation(UserDetail userDetail, AdmnOperation admnOperation, Object request) {
    	AdminUserLogEntity log = buildLog(admnOperation, userDetail);
    	log.setRequestParameters(request);
    	save(log);
    	return log;
    }
    
    public static AdminUserLogEntity buildLog(OperatorLogInfo operatorLog) {
    	AdminUserLogEntity log = new AdminUserLogEntity();
		log.setOperationAt(new Date());
		String userAgent = operatorLog.getUserAgent();
		log.setUserAgent(userAgent);
		log.setBrowser(RequestUtils.getBrowerByAgent(userAgent));
		log.setRequestMethod(operatorLog.getRequestMethod());
		log.setRequestUrl(operatorLog.getUrl());
		log.setUserIp(operatorLog.getRemoteAddr());
		log.setUserName(operatorLog.getOperatorName());
		log.setUserId((Long)operatorLog.getOperatorId());
		log.setIsSuccess(operatorLog.isSuccess());
		log.setRequestParameters(operatorLog.getParameters());
		return log;
    }
    
    public static AdminUserLogEntity buildLog(AdmnOperation admnOperation, UserDetail userDetail) {
		AdminUserLogEntity log = new AdminUserLogEntity();
		log.setOperationAt(new Date());
		if (WebHolder.getRequest().isPresent()) {
			HttpServletRequest request = WebHolder.getRequest().get();
			String userAgent = RequestUtils.getUserAgent(request);
			log.setUserAgent(userAgent);
			log.setBrowser(RequestUtils.getBrowerByAgent(userAgent));
			log.setRequestMethod(request.getMethod());
			log.setRequestUrl(RequestUtils.getUrlPathHelper().getLookupPathForRequest(request));
			log.setUserIp(RequestUtils.getRemoteAddr(request));
		}
		if (userDetail!=null) {
			log.setUserName(userDetail.getUserName());
			log.setUserId(userDetail.getUserId());
		} else {
			log.setUserId(-1L); // ANONYMOUS USER
			log.setUserName(AdminUserLogEntity.ANONYMOUS_USER);
		}
		log.setOperationName(admnOperation.getName());
		log.setOperationCode(admnOperation.getCode());
		return log;
	}
}