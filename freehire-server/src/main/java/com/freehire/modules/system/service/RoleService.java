package com.freehire.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.common.base.BasePageQuery;
import com.freehire.modules.system.dto.RoleDTO;
import com.freehire.modules.system.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface RoleService {

    /**
     * 分页查询角色
     */
    IPage<Role> getRolePage(BasePageQuery query);

    /**
     * 获取所有角色
     */
    List<Role> getAllRoles();

    /**
     * 获取角色详情
     */
    Role getRoleById(Long id);

    /**
     * 获取用户的角色列表
     */
    List<Role> getRolesByUserId(Long userId);

    /**
     * 新增角色
     */
    Long createRole(RoleDTO dto);

    /**
     * 更新角色
     */
    void updateRole(RoleDTO dto);

    /**
     * 删除角色
     */
    void deleteRole(Long id);

    /**
     * 分配菜单权限
     */
    void assignMenus(Long roleId, List<Long> menuIds);

    /**
     * 获取角色的菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);
}

