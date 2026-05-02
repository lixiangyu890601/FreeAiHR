package com.freehire.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freehire.modules.system.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单Mapper
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 查询用户的菜单
     */
    @Select("""
            SELECT DISTINCT m.id, m.parent_id AS parentId, m.menu_name AS name, 
                   m.menu_type AS type, m.path, m.component, m.permission, 
                   m.icon, m.sort, m.status, m.visible, m.deleted, 
                   m.create_time AS createTime, m.update_time AS updateTime
            FROM sys_menu m
            INNER JOIN sys_role_menu rm ON m.id = rm.menu_id
            INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id
            WHERE ur.user_id = #{userId} AND m.deleted = 0
            ORDER BY m.sort
            """)
    List<Menu> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询所有菜单
     */
    @Select("""
            SELECT id, parent_id AS parentId, menu_name AS name, 
                   menu_type AS type, path, component, permission, 
                   icon, sort, status, visible, deleted,
                   create_time AS createTime, update_time AS updateTime
            FROM sys_menu WHERE deleted = 0 ORDER BY sort
            """)
    List<Menu> selectAllMenus();

    /**
     * 查询用户的权限标识
     */
    @Select("""
            SELECT DISTINCT m.permission FROM sys_menu m
            INNER JOIN sys_role_menu rm ON m.id = rm.menu_id
            INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id
            WHERE ur.user_id = #{userId} AND m.permission IS NOT NULL AND m.deleted = 0
            """)
    List<String> selectPermissionsByUserId(@Param("userId") Long userId);
}

