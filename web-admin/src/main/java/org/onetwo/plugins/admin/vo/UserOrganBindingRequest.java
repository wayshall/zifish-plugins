package org.onetwo.plugins.admin.vo;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
public class UserOrganBindingRequest {
	@NotNull
	Long dataId;
//	@NotNull
	Long bindingId;
}

