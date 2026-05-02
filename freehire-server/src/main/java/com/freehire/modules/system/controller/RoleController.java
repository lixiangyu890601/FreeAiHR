package com.freehire.modules.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.common.base.BasePageQuery;
import com.freehire.common.response.PageResult;
import com.freehire.common.response.R;
import com.freehire.modules.system.dto.RoleDTO;
import com.freehire.modules.system.entity.Role;
import com.freehire.modules.system.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public R<PageResult<Role>> getRolePage(BasePageQuery query) {
        IPage<Role> page = roleService.getRolePage(query);
        return R.ok(PageResult.of(page));
    }

    @Operation(summary = "获取所有角色")
    @GetMapping("/list")
    public R<List<Role>> getAllRoles() {
        return R.ok(roleService.getAllRoles());
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public R<Role> getRoleById(@PathVariable Long id) {
        return R.ok(roleService.getRoleById(id));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public R<Long> createRole(@Valid @RequestBody RoleDTO dto) {
        return R.ok(roleService.createRole(dto));
    }

    @Operation(summary = "更新角色")
    @PutMapping
    public R<Void> updateRole(@Valid @RequestBody RoleDTO dto) {
        roleService.updateRole(dto);
        return R.ok();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public R<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return R.ok();
    }

    @Operation(summary = "获取角色菜单ID")
    @GetMapping("/{id}/menus")
    public R<List<Long>> getRoleMenuIds(@PathVariable Long id) {
        return R.ok(roleService.getRoleMenuIds(id));
    }

    @Operation(summary = "分配菜单权限")
    @PostMapping("/{id}/menus")
    public R<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(id, menuIds);
        return R.ok();
    }
}

