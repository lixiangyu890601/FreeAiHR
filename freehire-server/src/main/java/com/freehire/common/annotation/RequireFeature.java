package com.freehire.common.annotation;

import java.lang.annotation.*;

/**
 * 功能权限注解
 * 标注在Controller方法上，检查当前套餐是否拥有该功能
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireFeature {

    /**
     * 功能代码
     * 可选值: ai_parse, ai_match, ai_generate_jd, talent_pool, data_report, career_site, api_access
     */
    String value();

    /**
     * 功能名称（用于错误提示）
     */
    String name() default "";
}

