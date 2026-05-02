package com.freehire.common.annotation;

import java.lang.annotation.*;

/**
 * 用量检查注解
 * 标注在Controller方法上，检查当前用量是否超限
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckQuota {

    /**
     * 用量类型
     * 可选值: job, resume, user, ai_parse, ai_match
     */
    String value();

    /**
     * 用量名称（用于错误提示）
     */
    String name() default "";
}

