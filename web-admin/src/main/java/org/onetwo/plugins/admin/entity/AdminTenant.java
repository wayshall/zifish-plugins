package org.onetwo.plugins.admin.entity;

import org.onetwo.common.spring.copier.BeanCloneable;
import org.onetwo.dbm.jpa.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminTenant extends BaseEntity implements BeanCloneable {
    /**
     * 租户Id
     */
    Long id;
    String name;
    
}
