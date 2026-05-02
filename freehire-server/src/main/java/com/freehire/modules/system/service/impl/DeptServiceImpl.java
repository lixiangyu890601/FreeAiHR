package com.freehire.modules.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freehire.common.exception.BusinessException;
import com.freehire.modules.system.dto.DeptDTO;
import com.freehire.modules.system.entity.Dept;
import com.freehire.modules.system.mapper.DeptMapper;
import com.freehire.modules.system.service.DeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门服务实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper;

    @Override
    public List<Dept> getDeptTree() {
        List<Dept> allDepts = deptMapper.selectAllDepts();
        return buildTree(allDepts, 0L);
    }

    @Override
    public List<Dept> getDeptList() {
        return deptMapper.selectAllDepts();
    }

    @Override
    public Dept getDeptById(Long id) {
        Dept dept = deptMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        return dept;
    }

    @Override
    @Transactional
    public Long createDept(DeptDTO dto) {
        Dept dept = new Dept();
        BeanUtil.copyProperties(dto, dept);

        if (dept.getParentId() == null) {
            dept.setParentId(0L);
        }

        if (StpUtil.isLogin()) {
            dept.setCreateBy(StpUtil.getLoginIdAsLong());
        }

        deptMapper.insert(dept);

        log.info("创建部门成功: {}", dept.getId());
        return dept.getId();
    }

    @Override
    @Transactional
    public void updateDept(DeptDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("部门ID不能为空");
        }

        Dept existing = deptMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("部门不存在");
        }

        Dept dept = new Dept();
        BeanUtil.copyProperties(dto, dept);

        if (StpUtil.isLogin()) {
            dept.setUpdateBy(StpUtil.getLoginIdAsLong());
        }

        deptMapper.updateById(dept);

        log.info("更新部门成功: {}", dto.getId());
    }

    @Override
    @Transactional
    public void deleteDept(Long id) {
        Dept dept = deptMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查是否有子部门
        List<Dept> children = deptMapper.selectByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("存在子部门，不能删除");
        }

        deptMapper.deleteById(id);

        log.info("删除部门成功: {}", id);
    }

    private List<Dept> buildTree(List<Dept> allDepts, Long parentId) {
        Map<Long, List<Dept>> parentMap = allDepts.stream()
                .collect(Collectors.groupingBy(Dept::getParentId));

        List<Dept> roots = parentMap.getOrDefault(parentId, new ArrayList<>());
        for (Dept dept : roots) {
            dept.setChildren(buildTree(allDepts, dept.getId()));
        }
        return roots;
    }
}

