package org.onetwo.plugins.admin.vo;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.onetwo.plugins.admin.entity.AdminUserBinding.BindingUserId;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */

@Data
public class UserBindingRequest {

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
    private String avatar;
    
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

}

