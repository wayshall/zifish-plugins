package org.onetwo.plugins.admin.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AdminOperationCodes implements AdmnOperation {
	/***
	 * 登录
	 */
	LOGIN("登录"),
	/***
	 * 修改密码
	 */
	CHANGE_PWD("修改密码");
	
	@Getter
	String name;

	@Override
	public String getCode() {
		return name();
	}
	
}
