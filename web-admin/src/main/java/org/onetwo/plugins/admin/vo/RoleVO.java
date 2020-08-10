package org.onetwo.plugins.admin.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
@Data
public class RoleVO implements Serializable {
	
	Long id;
	String code;
	String name;
    Long tenantId;

}
