package com.freehire.modules.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.common.response.PageResult;
import com.freehire.common.response.R;
import com.freehire.modules.system.dto.UserDTO;
import com.freehire.modules.system.dto.UserQuery;
import com.freehire.modules.system.entity.User;
import com.freehire.modules.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public R<PageResult<User>> getUserPage(UserQuery query) {
        IPage<User> page = userService.getUserPage(query);
        return R.ok(PageResult.of(page));
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public R<User> getUserById(@PathVariable Long id) {
        return R.ok(userService.getUserById(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public R<Long> createUser(@Valid @RequestBody UserDTO dto) {
        return R.ok(userService.createUser(dto));
    }

    @Operation(summary = "更新用户")
    @PutMapping
    public R<Void> updateUser(@Valid @RequestBody UserDTO dto) {
        userService.updateUser(dto);
        return R.ok();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public R<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return R.ok();
    }

    @Operation(summary = "重置密码")
    @PostMapping("/{id}/reset-password")
    public R<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String newPassword = params.getOrDefault("password", "123456");
        userService.resetPassword(id, newPassword);
        return R.ok();
    }

    @Operation(summary = "修改密码")
    @PostMapping("/change-password")
    public R<Void> changePassword(@RequestBody Map<String, String> params) {
        Long userId = StpUtil.getLoginIdAsLong();
        userService.changePassword(userId, params.get("oldPassword"), params.get("newPassword"));
        return R.ok();
    }

    @Operation(summary = "获取用户角色ID")
    @GetMapping("/{id}/roles")
    public R<List<Long>> getUserRoleIds(@PathVariable Long id) {
        return R.ok(userService.getUserRoleIds(id));
    }

    @Operation(summary = "分配角色")
    @PostMapping("/{id}/roles")
    public R<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return R.ok();
    }
}

