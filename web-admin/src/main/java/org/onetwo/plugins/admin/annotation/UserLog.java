package org.onetwo.plugins.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserLog {
	/***
	 * operationName
	 * @author weishao zeng
	 * @return
	 */
	String value();
	
	/***
	 * 操作代码
	 * @author weishao zeng
	 * @return
	 */
	String operationCode() default "";
}
