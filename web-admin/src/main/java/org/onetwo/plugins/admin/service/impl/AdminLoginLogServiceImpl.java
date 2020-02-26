
package org.onetwo.plugins.admin.service.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.utils.Assert;
import org.onetwo.common.utils.Page;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.common.web.utils.RequestUtils;
import org.onetwo.common.web.utils.WebHolder;
import org.onetwo.dbm.core.internal.DbmCrudServiceImpl;
import org.onetwo.plugins.admin.dao.AdminUserDao;
import org.onetwo.plugins.admin.entity.AdminLoginLogEntity;
import org.onetwo.plugins.admin.utils.AdmnOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminLoginLogServiceImpl extends DbmCrudServiceImpl<AdminLoginLogEntity, Long> {
	
    @Autowired
    public AdminLoginLogServiceImpl(BaseEntityManager baseEntityManager) {
        super(baseEntityManager);
    }
    
    @Transactional(readOnly=true)
    public Page<AdminLoginLogEntity> findPage(Page<AdminLoginLogEntity> page, AdminLoginLogEntity example) {
        return baseEntityManager.from(entityClass)
                                .where()
                                    .addFields(example)
                                    .ignoreIfNull()
                                .end()
                                .toQuery()
                                .page(page);
    }
    
    public static AdminLoginLogEntity buildLog(AdmnOperation admnOperation, UserDetail userDetail) {
		AdminLoginLogEntity log = new AdminLoginLogEntity();
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
			log.setUserName("ANONYMOUS");
		}
		log.setOperationCode(admnOperation.getCode());
		return log;
	}
}