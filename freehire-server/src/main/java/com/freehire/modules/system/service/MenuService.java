package com.freehire.modules.system.service;

import com.freehire.modules.system.entity.Menu;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface MenuService {

    /**
     * 获取菜单树
     */
    List<Menu> getMenuTree();

    /**
     * 获取用户菜单树
     */
    List<Menu> getUserMenuTree(Long userId);

    /**
     * 获取用户权限列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 获取菜单详情
     */
    Menu getMenuById(Long id);
}

