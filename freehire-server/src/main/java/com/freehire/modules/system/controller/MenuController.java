package com.freehire.modules.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.freehire.common.response.R;
import com.freehire.modules.system.entity.Menu;
import com.freehire.modules.system.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public R<List<Menu>> getMenuTree() {
        return R.ok(menuService.getMenuTree());
    }

    @Operation(summary = "获取当前用户菜单")
    @GetMapping("/user-menu")
    public R<List<Menu>> getUserMenuTree() {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(menuService.getUserMenuTree(userId));
    }

    @Operation(summary = "获取当前用户权限")
    @GetMapping("/user-permissions")
    public R<List<String>> getUserPermissions() {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(menuService.getUserPermissions(userId));
    }

    @Operation(summary = "获取菜单详情")
    @GetMapping("/{id}")
    public R<Menu> getMenuById(@PathVariable Long id) {
        return R.ok(menuService.getMenuById(id));
    }
}

