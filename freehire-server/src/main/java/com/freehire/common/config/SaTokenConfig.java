package com.freehire.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器，打开注解式鉴权功能
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 指定需要登录认证的路由
            SaRouter.match("/**")
                    // 排除不需要登录的接口
                    .notMatch(
                            // 登录相关
                            "/auth/login",
                            "/auth/register",
                            "/auth/captcha",
                            // 健康检查
                            "/health",
                            // 公开招聘页面
                            "/public/**",
                            "/careers/**",
                            // 文件访问（简历下载等）
                            "/files/**",
                            // API文档
                            "/doc.html",
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/webjars/**",
                            // 静态资源
                            "/favicon.ico",
                            "/error"
                    )
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}

