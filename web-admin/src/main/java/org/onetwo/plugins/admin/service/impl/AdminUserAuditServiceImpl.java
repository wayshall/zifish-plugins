
package org.onetwo.plugins.admin.service.impl;

import java.util.Date;

import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.utils.Page;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.dbm.core.internal.DbmCrudServiceImpl;
import org.onetwo.plugins.admin.entity.AdminLoginLogEntity;
import org.onetwo.plugins.admin.entity.AdminUserAudit;
import org.onetwo.plugins.admin.utils.AdminOperationCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminUserAuditServiceImpl extends DbmCrudServiceImpl<AdminUserAudit, Long> {

	@Autowired
	private AdminLoginLogServiceImpl adminLoginLogService;
	
    @Autowired
    public AdminUserAuditServiceImpl(BaseEntityManager baseEntityManager) {
        super(baseEntityManager);
    }
    
    public void saveChangePwdAudit(UserDetail loginUser) {
    	AdminUserAudit userAuit = baseEntityManager.load(entityClass, loginUser.getUserId());
    	userAuit.setLastChangePwdAt(new Date());
    	save(userAuit);
		AdminLoginLogEntity log = AdminLoginLogServiceImpl.buildLog(AdminOperationCodes.CHANGE_PWD, loginUser);
		adminLoginLogService.save(log);
    }
    
    public void saveUserLoginAudit(AdminLoginLogEntity log) {
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
    	AdminUserAudit userAuit = baseEntityManager.findById(entityClass, userId);
    	return userAuit;
    }
    
    @Transactional(readOnly=true)
    public Page<AdminUserAudit> findPage(Page<AdminUserAudit> page, AdminUserAudit example) {
        return baseEntityManager.from(entityClass)
                                .where()
                                    .addFields(example)
                                    .ignoreIfNull()
                                .end()
                                .toQuery()
                                .page(page);
    }
}