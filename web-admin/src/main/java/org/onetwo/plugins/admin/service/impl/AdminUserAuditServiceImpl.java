
package org.onetwo.plugins.admin.service.impl;

import java.util.Date;

import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.utils.Page;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.dbm.core.internal.DbmCrudServiceImpl;
import org.onetwo.plugins.admin.entity.AdminUserAudit;
import org.onetwo.plugins.admin.entity.AdminUserLogEntity;
import org.onetwo.plugins.admin.utils.AdminOperationCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminUserAuditServiceImpl extends DbmCrudServiceImpl<AdminUserAudit, Long> {

	@Autowired
	private AdminUserLogServiceImpl adminLoginLogService;
	
    @Autowired
    public AdminUserAuditServiceImpl(BaseEntityManager baseEntityManager) {
        super(baseEntityManager);
    }
    
    public void saveChangePwdAudit(UserDetail loginUser) {
    	AdminUserAudit userAuit = this.findById(loginUser.getUserId());
    	if (userAuit==null) {
    		userAuit = new AdminUserAudit();
    		userAuit.setUserId(loginUser.getUserId());
    		userAuit.setUserName(loginUser.getUserName());
    	}
    	userAuit.setLastChangePwdAt(new Date());
    	save(userAuit);
    	
		AdminUserLogEntity log = AdminUserLogServiceImpl.buildLog(AdminOperationCodes.CHANGE_PWD, loginUser);
		adminLoginLogService.save(log);
    }
    
    public void saveUserLoginAudit(AdminUserLogEntity log) {
    	AdminUserAudit userAudit = findById(log.getUserId());
    	if (userAudit==null) {
    		userAudit = new AdminUserAudit();
    		userAudit.setUserId(log.getUserId());
    		userAudit.setUserName(log.getUserName());
    	}
    	userAudit.setLastLoginLogId(log.getId());
    	userAudit.setLastLoginAt(log.getOperationAt());
    	save(userAudit);
    }

    public AdminUserAudit findById(Long userId) {
    	AdminUserAudit userAuit = getBaseEntityManager().findById(entityClass, userId);
    	return userAuit;
    }
    
    @Transactional(readOnly=true)
    public Page<AdminUserAudit> findPage(Page<AdminUserAudit> page, AdminUserAudit example) {
        return getBaseEntityManager().from(entityClass)
                                .where()
                                    .addFields(example)
                                    .ignoreIfNull()
                                .end()
                                .toQuery()
                                .page(page);
    }
}