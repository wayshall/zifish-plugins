package org.onetwo.plugins.admin.captcha;

import org.onetwo.boot.module.redis.RedisOperationService;
import org.onetwo.common.log.JFishLoggerFactory;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.web.captcha.CaptchaChecker;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @author weishao zeng
 * <br/>
 */
public class RedisCaptchaChecker implements CaptchaChecker {
	final private long expireInSeconds;
	
	@Autowired
	private RedisOperationService redisOperationService;

	public RedisCaptchaChecker(int validInSeconds) {
		super();
		Assert.isTrue(validInSeconds>0, "validInSeconds["+validInSeconds+"] must greater than 0");
		this.expireInSeconds = validInSeconds;
	}
	
	public boolean check(String code, String redisKey){
		return check(code, redisKey, false);
	}
	
	public boolean check(String code, String redisKey, boolean debug){
		String key = getCaptchaKey(redisKey);
		String value = this.redisOperationService.getString(key);
		if (debug) {
			Logger logger = JFishLoggerFactory.getCommonLogger();
			logger.info("request code: {}, redis code: {}, key: {}", code, value, value);
		}
		return code.equalsIgnoreCase(value);
	}
	
	public CaptchaSignedResult encode(String code){
		long validTime = System.currentTimeMillis() + (expireInSeconds * 1000);
		String redisKey = LangUtils.randomUUID();
		redisOperationService.setString(getCaptchaKey(redisKey), code, expireInSeconds);
		return new CaptchaSignedResult(redisKey, validTime);
	}
	
	private String getCaptchaKey(String key) {
		return "captcha:" + key;
	}
	
}

