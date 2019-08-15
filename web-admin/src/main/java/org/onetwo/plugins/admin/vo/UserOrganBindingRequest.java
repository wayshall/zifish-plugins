package org.onetwo.plugins.admin.vo;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
public class UserOrganBindingRequest {
	@NotNull
	Long dataId;
	@NotNull
	Long bindingId;
}

