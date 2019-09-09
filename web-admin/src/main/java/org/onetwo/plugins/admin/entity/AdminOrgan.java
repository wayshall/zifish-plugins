package org.onetwo.plugins.admin.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.onetwo.common.spring.copier.BeanCloneable;
import org.onetwo.dbm.jpa.BaseEntity;
import org.onetwo.plugins.admin.utils.Enums.OrganStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="admin_organ")
public class AdminOrgan extends BaseEntity implements BeanCloneable {
    /**
     * 租户Id
     */
	@Id
    Long id;
    String name;
    @Enumerated(EnumType.STRING)
    OrganStatus status;
}
