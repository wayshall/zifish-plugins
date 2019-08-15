package org.onetwo.plugins.admin.service.impl;


import java.util.Collections;
import java.util.List;

import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.utils.Page;
import org.onetwo.common.utils.StringUtils;
import org.onetwo.plugins.admin.entity.AdminOrgan;
import org.onetwo.plugins.admin.view.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author weishao zeng
 * <br/>
 */
@Service
@Transactional
public class AdminOrganServiceImpl {

    @Autowired
    private BaseEntityManager baseEntityManager;
    
    public AdminOrgan load(Long id) {
    	return baseEntityManager.load(AdminOrgan.class, id);
    }
    
    public Page<AdminOrgan> findPage(PageRequest pageRequest, AdminOrgan adminOrgan) {
    	return this.baseEntityManager.from(AdminOrgan.class)
    							.where()
    								.addFields(adminOrgan, true)
    							.toQuery()
    							.page(pageRequest.toPageObject());
    }
    
    public List<AdminOrgan> serach(String query) {
    	if (StringUtils.isBlank(query)) {
    		return Collections.emptyList();
    	}
    	return this.baseEntityManager.from(AdminOrgan.class)
    							.where()
    								.operatorFields(new String[] {"id", "name:like"}, new Object[] {query})
    							.end()
    							.limit(0, 10)
    							.toQuery()
    							.list();
    }
}

