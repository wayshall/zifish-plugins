package org.onetwo.plugins.admin.event;

import java.util.List;

import org.onetwo.plugins.admin.vo.CreateOrUpdateAdminUserRequest;
import org.springframework.context.ApplicationEvent;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
@ToString
public class CreateOrUpdateAdminUserEvent extends ApplicationEvent {
	
	@Getter
	private List<CreateOrUpdateAdminUserRequest> adminUsers;

	@Builder
	public CreateOrUpdateAdminUserEvent(Object source, List<CreateOrUpdateAdminUserRequest> adminUsers) {
		super(source);
		this.adminUsers = adminUsers;
	}
}
