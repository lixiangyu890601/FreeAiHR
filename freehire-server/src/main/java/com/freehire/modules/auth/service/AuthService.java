package com.freehire.modules.auth.service;

import com.freehire.modules.auth.dto.LoginRequest;
import com.freehire.modules.auth.dto.LoginResponse;

/**
 * 认证服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    LoginResponse getCurrentUserInfo();
}

