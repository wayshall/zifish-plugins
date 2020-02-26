package org.onetwo.plugins.admin.utils;

public enum AdminOperationCodes implements AdmnOperation {
	/***
	 * 登录
	 */
	LOGIN,
	/***
	 * 修改密码
	 */
	CHANGE_PWD;

	@Override
	public String getCode() {
		return name();
	}
	
}
