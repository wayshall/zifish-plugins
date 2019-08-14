package org.onetwo.plugins.admin.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindUserByRoleQuery {

	List<String> roleNames;
	List<Long> roleIds;

}

