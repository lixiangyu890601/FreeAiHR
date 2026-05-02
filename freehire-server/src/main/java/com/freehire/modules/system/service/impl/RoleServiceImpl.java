package com.freehire.modules.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freehire.common.base.BasePageQuery;
import com.freehire.common.exception.BusinessException;
import com.freehire.modules.system.dto.RoleDTO;
import com.freehire.modules.system.entity.Role;
import com.freehire.modules.system.mapper.RoleMapper;
import com.freehire.modules.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public IPage<Role> getRolePage(BasePageQuery query) {
        Page<Role> page = new Page<>(query.getCurrent(), query.getSize());
        return roleMapper.selectPage(page, new LambdaQueryWrapper<Role>().orderByAsc(Role::getSort));
    }

    @Override
    public List<Role> getAllRoles() {
        return roleMapper.selectList(new LambdaQueryWrapper<Role>()
                .eq(Role::getStatus, 1)
                .orderByAsc(Role::getSort));
    }

    @Override
    public Role getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        // 加载菜单ID
        role.setMenuIds(roleMapper.selectMenuIdsByRoleId(id));
        return role;
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return roleMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public Long createRole(RoleDTO dto) {
        // 检查编码是否已存在
        Role existing = roleMapper.selectByCode(dto.getRoleCode());
        if (existing != null) {
            throw new BusinessException("角色编码已存在");
        }

        Role role = new Role();
        BeanUtil.copyProperties(dto, role);

        if (StpUtil.isLogin()) {
            role.setCreateBy(StpUtil.getLoginIdAsLong());
        }

        roleMapper.insert(role);

        // 分配菜单
        if (dto.getMenuIds() != null && !dto.getMenuIds().isEmpty()) {
            assignMenus(role.getId(), dto.getMenuIds());
        }

        log.info("创建角色成功: {}", role.getId());
        return role.getId();
    }

    @Override
    @Transactional
    public void updateRole(RoleDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("角色ID不能为空");
        }

        Role existing = roleMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("角色不存在");
        }

        Role role = new Role();
        BeanUtil.copyProperties(dto, role);

        if (StpUtil.isLogin()) {
            role.setUpdateBy(StpUtil.getLoginIdAsLong());
        }

        roleMapper.updateById(role);

        // 更新菜单
        if (dto.getMenuIds() != null) {
            assignMenus(dto.getId(), dto.getMenuIds());
        }

        log.info("更新角色成功: {}", dto.getId());
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 不能删除超级管理员角色
        if ("super_admin".equals(role.getRoleCode())) {
            throw new BusinessException("不能删除超级管理员角色");
        }

        roleMapper.deleteById(id);

        // 删除角色菜单关联
        roleMapper.deleteRoleMenuByRoleId(id);

        log.info("删除角色成功: {}", id);
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 先删除原有菜单
        roleMapper.deleteRoleMenuByRoleId(roleId);

        // 再添加新菜单
        for (Long menuId : menuIds) {
            roleMapper.insertRoleMenu(roleId, menuId);
        }
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMapper.selectMenuIdsByRoleId(roleId);
    }
}

