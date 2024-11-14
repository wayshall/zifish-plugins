package org.onetwo.plugins.admin.vo;

import java.util.Date;
import java.util.Set;

import org.onetwo.common.spring.copier.BeanCloneable;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
public class CreateOrUpdateAdminUserRequest implements BeanCloneable {
    private Long id;
	
    private String userName;

    private String nickName;

    private String password;

    private String email;

    private String mobile;

    private String gender;

    private String status;

    private Date birthday;


    private String avatar;
    
    private Long bindingUserId;
    
    private Set<Long> roleIds;
}
