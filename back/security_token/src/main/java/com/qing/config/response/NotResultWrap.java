package com.qing.config.response;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 不进行返回值包装注解
 * @author 竹秋廿四
 * @date 2021-01-13 20:55
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface NotResultWrap {
}
