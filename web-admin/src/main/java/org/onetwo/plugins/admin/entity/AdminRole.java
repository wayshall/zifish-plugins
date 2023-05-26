package org.onetwo.plugins.admin.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.onetwo.common.utils.StringUtils;
import org.onetwo.dbm.annotation.DbmTableIdGenerator;
import org.onetwo.plugins.admin.utils.Enums.CommonStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("serial")
@Entity
@Table(name="admin_role")
@Data
@EqualsAndHashCode(callSuper=true)
public class AdminRole extends AdminTenantable {
//	@Id
	@DbmTableIdGenerator("seq_admin_role")
    private Long id;

    private String name;

    private String code;

    private String status;

    private String remark;


    private String appCode;
    

    public String getStatusName(){
    	if(StringUtils.isBlank(status))
    		return "";
    	return CommonStatus.valueOf(status).getLabel();
    }
}