package com.freehire.modules.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freehire.common.exception.BusinessException;
import com.freehire.common.response.ResultCode;
import com.freehire.modules.auth.dto.LoginRequest;
import com.freehire.modules.auth.dto.LoginResponse;
import com.freehire.modules.auth.service.AuthService;
import com.freehire.modules.subscription.service.SubscriptionService;
import com.freehire.modules.system.entity.User;
import com.freehire.modules.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * 认证服务实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final SubscriptionService subscriptionService;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));

        if (user == null) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 校验状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 校验密码
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 登录
        StpUtil.login(user.getId());

        // 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 构建响应
        return buildLoginResponse(user);
    }

    @Override
    public LoginResponse getCurrentUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        return buildLoginResponse(user);
    }

    private LoginResponse buildLoginResponse(User user) {
        LoginResponse response = new LoginResponse();
        response.setToken(StpUtil.getTokenValue());
        response.setExpiresIn(StpUtil.getTokenTimeout());
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setAvatar(user.getAvatar());

        // TODO: 从数据库查询角色和权限
        response.setRoles(Collections.singletonList("admin"));
        response.setPermissions(Collections.singletonList("*"));

        // 添加订阅信息
        try {
            response.setSubscription(subscriptionService.getCurrentSubscription());
        } catch (Exception e) {
            log.warn("获取订阅信息失败", e);
        }

        return response;
    }
}

