
package org.onetwo.plugins.admin.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.onetwo.dbm.annotation.SnowflakeId;
import org.onetwo.dbm.ui.annotation.DUIEntity;
import org.onetwo.dbm.ui.annotation.DUIField;
import org.onetwo.dbm.ui.annotation.DUIInput;

import lombok.Data;

/***
 * admin_用户审计表
 */
@SuppressWarnings("serial")
@Entity
@Table(name="admin_user_audit")
@Data
//@EqualsAndHashCode(callSuper=true)
@DUIEntity(
        name = "AdminUserAudit", 
        label = "用户审计"
)
public class AdminUserAudit implements Serializable {

    @SnowflakeId
    Long userId;
    /***
     * 用户名
     */
    @NotNull
    @Length(max=50)
    @DUIField(label = "用户名", order = 3)
    String userName;
    
    Long lastLoginLogId;
    
    /***
     * 最近一次登录时间
     */
    @DUIInput(type=DUIInput.InputTypes.DATE_TIME)
    @DUIField(label = "最近一次登录时间", order = 1)
    Date lastLoginAt;
    
    
    /***
     * 
     */
    @DUIInput(type=DUIInput.InputTypes.DATE_TIME)
    @DUIField(label = "", order = 4)
    Date lastChangePwdAt;
    
}