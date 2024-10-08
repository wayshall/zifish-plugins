package org.onetwo.plugins.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onetwo.boot.captcha.CaptchaProps;
import org.onetwo.common.web.captcha.CaptchaChecker.CaptchaSignedResult;
import org.onetwo.common.web.captcha.SimpleCaptchaGenerator;
import org.onetwo.common.web.captcha.SimpleCaptchaGenerator.CaptchaResult;
import org.onetwo.common.web.captcha.SimpleCaptchaGenerator.CaptchaSettings;
import org.onetwo.common.web.utils.ResponseUtils;
import org.onetwo.ext.security.utils.SecurityConfig;
import org.onetwo.plugins.admin.utils.WebAdminProperties;
import org.onetwo.plugins.admin.vo.CaptchaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//@RestController
@RequestMapping("/captcha")
public class CaptchaController extends WebAdminBaseController {
	@Autowired
	private WebAdminProperties webAdminProperties;
	@Autowired
	private SecurityConfig securityConfig;
	private SimpleCaptchaGenerator captchaGenerator = new SimpleCaptchaGenerator();

	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public CaptchaResponse captcha(HttpServletRequest request, HttpServletResponse response){
		CaptchaProps props = webAdminProperties.getCaptcha();
		CaptchaSettings settings = props.getSettings();
//		settings.setCodeColor(props.getColor());
		CaptchaResult result = captchaGenerator.generate(settings);
		CaptchaResponse res = new CaptchaResponse();
		res.setData(result.getDataAsBase64());
//		res.setSign(webAdminProperties.getCaptcha().sign(result.getCode()));
		// 以后修改为支持redis storer
		CaptchaSignedResult sign = webAdminProperties.getCaptchaChecker().encode(result.getCode());
		ResponseUtils.setHttpOnlyCookie(response, props.getCookieName(), sign.getSigned(), securityConfig.getCookie().getPath(), -1, null);
		res.setSign(sign.getSigned());
		return res;
	}

}
