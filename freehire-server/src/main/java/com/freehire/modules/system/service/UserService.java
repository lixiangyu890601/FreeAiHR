package com.freehire.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.modules.system.dto.UserDTO;
import com.freehire.modules.system.dto.UserQuery;
import com.freehire.modules.system.entity.User;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 分页查询用户
     */
    IPage<User> getUserPage(UserQuery query);

    /**
     * 获取用户详情
     */
    User getUserById(Long id);

    /**
     * 根据用户名查询
     */
    User getUserByUsername(String username);

    /**
     * 新增用户
     */
    Long createUser(UserDTO dto);

    /**
     * 更新用户
     */
    void updateUser(UserDTO dto);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 重置密码
     */
    void resetPassword(Long id, String newPassword);

    /**
     * 修改密码
     */
    void changePassword(Long id, String oldPassword, String newPassword);

    /**
     * 获取用户角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 分配用户角色
     */
    void assignRoles(Long userId, List<Long> roleIds);
}

