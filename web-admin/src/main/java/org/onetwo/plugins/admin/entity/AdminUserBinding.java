package org.onetwo.plugins.admin.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.onetwo.plugins.admin.entity.AdminUserBinding.BindingUserId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author weishao zeng
 * <br/>
 */
@Deprecated
@Entity
@Table(name="admin_user_binding")
@Data
@IdClass(BindingUserId.class)
public class AdminUserBinding {

    @Id
    @NotNull(message="后台用户不能为null")
    private Long adminUserId;
    /***
     * 
     */
    @Id
    @NotNull(message="要绑定的用户id不能为null")
    private Long bindingUserId;
    
    private String bindingUserName;
    
	@Transient
	private BindingUserId id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date bindingAt;
	

	public BindingUserId getId() {
		return new BindingUserId(adminUserId, bindingUserId);
	}
	
	public void setId(BindingUserId id) {
		this.id = id;
		this.adminUserId = id.getAdminUserId();
		this.bindingUserId = id.getBindingUserId();
	}

    @SuppressWarnings("serial")
	@Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Deprecated
    public static class BindingUserId implements Serializable {
        @Id
        @NotNull
        private Long adminUserId;
        
        @Id
        @NotNull
        private Long bindingUserId;

    }
}

