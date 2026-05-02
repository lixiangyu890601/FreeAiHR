package com.freehire.modules.system.controller;

import com.freehire.common.response.R;
import com.freehire.modules.system.dto.DeptDTO;
import com.freehire.modules.system.entity.Dept;
import com.freehire.modules.system.service.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @Operation(summary = "获取部门树")
    @GetMapping("/tree")
    public R<List<Dept>> getDeptTree() {
        return R.ok(deptService.getDeptTree());
    }

    @Operation(summary = "获取部门列表")
    @GetMapping("/list")
    public R<List<Dept>> getDeptList() {
        return R.ok(deptService.getDeptList());
    }

    @Operation(summary = "获取部门详情")
    @GetMapping("/{id}")
    public R<Dept> getDeptById(@PathVariable Long id) {
        return R.ok(deptService.getDeptById(id));
    }

    @Operation(summary = "新增部门")
    @PostMapping
    public R<Long> createDept(@Valid @RequestBody DeptDTO dto) {
        return R.ok(deptService.createDept(dto));
    }

    @Operation(summary = "更新部门")
    @PutMapping
    public R<Void> updateDept(@Valid @RequestBody DeptDTO dto) {
        deptService.updateDept(dto);
        return R.ok();
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    public R<Void> deleteDept(@PathVariable Long id) {
        deptService.deleteDept(id);
        return R.ok();
    }
}

