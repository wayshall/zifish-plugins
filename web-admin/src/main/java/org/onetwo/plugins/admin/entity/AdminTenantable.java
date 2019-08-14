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
import org.onetwo.plugins.admin.entity.AdminTenantable.TenantFieldListener;
import org.onetwo.plugins.admin.vo.AdminLoginUserInfo;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableMap;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@DataQueryParamaterEnhancer(TenantFieldListener.class)
public class AdminTenantable extends BaseEntity implements BeanCloneable {
    /**
     * 租户Id
     */
    @DbmFieldListeners(TenantFieldListener.class)
    Long tenantId;
    

    /***
     * baseEntitymanger.findBy*方法查询自动增加过滤条件
     * @author wayshall
     *
     */
    public static class TenantFieldListener implements IDataQueryParamterEnhancer, DbmEntityFieldListener {
    	@Autowired
    	private SessionUserManager<UserDetail> sessionUserManager;
    	
    	
        @Override
		public Object beforeFieldInsert(DbmMappedField field, Object fieldValue) {
        	Optional<AdminLoginUserInfo> adminUser = getAdminUser();
        	if (adminUser.isPresent()) {
                return fieldValue;
        	}
            Object newValue = adminUser.get().getTenantId();
			return newValue;
		}

		@Override
		public Object beforeFieldUpdate(DbmMappedField field, Object fieldValue) {
			return beforeFieldInsert(field, fieldValue);
		}

		@Override
        public Map<Object, Object> enhanceParameters(ExtQuery query) {
        	Optional<AdminLoginUserInfo> adminUser = getAdminUser();
        	if (adminUser.isPresent()) {
                return Collections.emptyMap();
        	}
        	if (adminUser.get().getTenantId()==null) {
                return Collections.emptyMap();
        	}
            return ImmutableMap.of("tenantId", adminUser.get().getTenantId());
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
