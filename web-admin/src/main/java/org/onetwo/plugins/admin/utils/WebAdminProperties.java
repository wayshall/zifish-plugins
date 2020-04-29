package org.onetwo.plugins.admin.utils;

import java.util.List;

import org.onetwo.common.spring.Springs;
import org.onetwo.common.web.captcha.AESCaptchaChecker;
import org.onetwo.common.web.captcha.CaptchaChecker;
import org.onetwo.common.web.captcha.Captchas;
import org.onetwo.common.web.captcha.SimpleCaptchaGenerator.CaptchaSettings;
import org.onetwo.ext.security.provider.CaptchaAuthenticationProvider;
import org.onetwo.plugins.admin.captcha.RedisCaptchaChecker;
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
	
	public static final String PREFIX = "jfish.plugin.web-admin";
	
	/****
	 * 是否强制初始用户修改密码
	 * 默认为false，不强制
	 */
	boolean forceModifyPassword;
	
	CaptchaProps captcha = new CaptchaProps();
	UserLogProps userlog = new UserLogProps();
	
	CaptchaChecker captchaChecker;
	
	public CaptchaChecker getCaptchaChecker(){
		Assert.hasText(captcha.getSalt(), "property[jfish.plugin.web-admin.captcha.salt] must has text!");
		if(captchaChecker==null){
			captchaChecker = captcha.coder.createChecker(captcha);
		}
		return captchaChecker;
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
	
	@Data
	public static class CaptchaProps {
		public static final String ENABLED_KEY = PREFIX+".captcha.enabled";
		
		private String salt;
		/**
		 * 与salt同义 
		 */
		private String key;
		private int expireInSeconds = Captchas.DEFAULT_VALID_IN_SECONDS;
		private String parameterName = CaptchaAuthenticationProvider.PARAMS_VERIFY_CODE;
		private String cookieName = CaptchaAuthenticationProvider.COOKIES_VERIFY_CODE;
		private CaptchaCoder coder = CaptchaCoder.SHA;
		
		CaptchaSettings settings = new CaptchaSettings();

		public void setSalt(String salt) {
			this.salt = salt;
			this.key = salt;
		}

		public void setKey(String key) {
			this.key = key;
			this.salt = key;
		}
		
	}
	
	public static enum CaptchaCoder {
		SHA {
			@Override
			public CaptchaChecker createChecker(CaptchaProps props) {
				return Captchas.createCaptchaChecker(props.getSalt(), props.getExpireInSeconds());
			}
		},
		
		AES {
			@Override
			public CaptchaChecker createChecker(CaptchaProps props) {
				return new AESCaptchaChecker(props.getSalt(), props.getExpireInSeconds());
			}
		},
		
		REDIS {
			@Override
			public CaptchaChecker createChecker(CaptchaProps props) {
				RedisCaptchaChecker checker = new RedisCaptchaChecker(props.getExpireInSeconds());
				Springs.getInstance().autoInject(checker);
				return checker;
			}
		};
		
		abstract public CaptchaChecker createChecker(CaptchaProps props);
	}
}
