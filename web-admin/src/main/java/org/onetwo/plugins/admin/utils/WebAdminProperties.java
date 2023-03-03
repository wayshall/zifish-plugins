package org.onetwo.plugins.admin.utils;

import java.util.List;

import org.onetwo.boot.captcha.CaptchaProps;
import org.onetwo.common.web.captcha.CaptchaChecker;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

import lombok.Data;

/**
 * @author wayshall
 * <br/>
 */
@ConfigurationProperties(prefix=WebAdminProperties.PREFIX)
@Data
public class WebAdminProperties {
	
	public static final String PREFIX = org.onetwo.boot.core.config.BootJFishConfig.ZIFISH_CONFIG_PREFIX+ ".plugin.web-admin";
	/***
	 * 
jfish:
    plugin:
        web-admin:
            captcha:
                salt: pAyL3e54wKC37wPc59oFoyRSp4wy0e9OpIxFHjQvQIO38Nv4VPXqbJZe2knLabXre3
                coder: redis
                settings: 
                    codeColor: 255, 0, 0
                    fontHeight: 0.99
	 */
	public static final String CAPTCHA_ENABLED_KEY = PREFIX+".captcha.enabled";
	
	/****
	 * 是否强制初始用户修改密码
	 * 默认为false，不强制
	 */
	boolean forceModifyPassword;
	
	CaptchaProps captcha = new CaptchaProps();
	UserLogProps userlog = new UserLogProps();
	
	LoginUserProps loginUser = new LoginUserProps();
	
	CaptchaChecker captchaChecker;
	
	public CaptchaChecker getCaptchaChecker(){
		Assert.hasText(captcha.getSalt(), "property[jfish.plugin.web-admin.captcha.salt] must has text!");
		if(captchaChecker==null){
			captchaChecker = captcha.getCoder().createChecker(captcha);
		}
		return captchaChecker;
	}
	

	@Data
	public static class LoginUserProps {
		/***
		 * 是否导出权限
		 */
		boolean exposeAuthorities;
	}

	@Data
	public static class UserLogProps {
		boolean logByPermission;
		/****
		 * 只记录put, post, delete方法
		 */
		List<String> requestMethods = Lists.newArrayList("put", "post", "delete");
		
		public boolean isLogRequestMethod(String method) {
			return requestMethods.contains(method.toLowerCase());
		}
	}
}
