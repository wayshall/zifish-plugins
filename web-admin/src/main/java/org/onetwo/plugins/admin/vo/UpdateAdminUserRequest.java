package org.onetwo.plugins.admin.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.onetwo.common.spring.validator.annotation.Mobile;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
public class UpdateAdminUserRequest {
	@NotBlank
	private String nickName;

    private String password;

    @Email
    private String email;

    @Mobile
    private String mobile;

    private String gender;
    private String status;
    
//    private String avatar;
}
