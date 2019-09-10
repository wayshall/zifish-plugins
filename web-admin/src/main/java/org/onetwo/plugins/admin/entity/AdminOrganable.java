package org.onetwo.plugins.admin.entity;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.persistence.MappedSuperclass;

import org.onetwo.common.db.filter.DataQueryParamaterEnhancer;
import org.onetwo.common.db.filter.IDataQueryParamterEnhancer;
import org.onetwo.common.db.sqlext.ExtQuery;
import org.onetwo.common.spring.copier.BeanCloneable;
import org.onetwo.common.web.userdetails.SessionUserManager;
import org.onetwo.common.web.userdetails.UserDetail;
import org.onetwo.dbm.annotation.DbmFieldListeners;
import org.onetwo.dbm.jpa.BaseEntity;
import org.onetwo.dbm.mapping.DbmEntityFieldListener;
import org.onetwo.dbm.mapping.DbmMappedField;
import org.onetwo.plugins.admin.entity.AdminOrganable.OrganFieldListener;
import org.onetwo.plugins.admin.vo.AdminLoginUserInfo;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableMap;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@DataQueryParamaterEnhancer(OrganFieldListener.class)
public class AdminOrganable extends BaseEntity implements BeanCloneable {
    /**
     * 租户Id
     */
    @DbmFieldListeners(OrganFieldListener.class)
    Long organId;
    

    /***
     * baseEntitymanger.findBy*方法查询自动增加过滤条件
     * @author wayshall
     *
     */
    public static class OrganFieldListener implements IDataQueryParamterEnhancer, DbmEntityFieldListener {
    	@Autowired
    	private SessionUserManager<UserDetail> sessionUserManager;
    	
    	
        @Override
		public Object beforeFieldInsert(DbmMappedField field, Object fieldValue) {
        	Object newValue = null;
        	Optional<AdminLoginUserInfo> adminUser = getAdminUser();
        	if (!adminUser.isPresent()) {
        		newValue = fieldValue;
        	} else {
        		newValue = adminUser.map(d -> d.getOrganId()).orElse((Long)fieldValue);
        	}
        	newValue = newValue==null?0:newValue;
			return newValue;
		}

		@Override
		public Object beforeFieldUpdate(DbmMappedField field, Object fieldValue) {
			return beforeFieldInsert(field, fieldValue);
		}

		@Override
        public Map<Object, Object> enhanceParameters(ExtQuery query) {
        	Optional<AdminLoginUserInfo> adminUser = getAdminUser();
        	if (!adminUser.isPresent()) {
                return Collections.emptyMap();
        	}
        	Long organId = adminUser.get().getOrganId();
        	if (organId==null || organId==0 || query.getParams().containsKey("organId")) {
                return Collections.emptyMap();
        	}
            return ImmutableMap.of("organId", adminUser.get().getOrganId());
        }
        
        private Optional<AdminLoginUserInfo> getAdminUser() {
        	UserDetail user = sessionUserManager.getCurrentUser();
        	if (user==null || !(user instanceof AdminLoginUserInfo)) {
                return Optional.empty();
        	}
        	AdminLoginUserInfo adminUser = (AdminLoginUserInfo) user;
        	return Optional.of(adminUser);
        }
    }
}
