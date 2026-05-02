package com.freehire.modules.auth.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.freehire.common.response.R;
import com.freehire.modules.auth.dto.LoginRequest;
import com.freehire.modules.auth.dto.LoginResponse;
import com.freehire.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return R.ok(response);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<Void> logout() {
        StpUtil.logout();
        return R.ok();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public R<LoginResponse> getUserInfo() {
        LoginResponse response = authService.getCurrentUserInfo();
        return R.ok(response);
    }

    @Operation(summary = "获取Token信息")
    @GetMapping("/token")
    public R<SaTokenInfo> getTokenInfo() {
        return R.ok(StpUtil.getTokenInfo());
    }
}

