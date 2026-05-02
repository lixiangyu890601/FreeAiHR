package com.freehire.modules.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freehire.common.exception.BusinessException;
import com.freehire.modules.subscription.service.SubscriptionService;
import com.freehire.modules.system.dto.UserDTO;
import com.freehire.modules.system.dto.UserQuery;
import com.freehire.modules.system.entity.User;
import com.freehire.modules.system.mapper.RoleMapper;
import com.freehire.modules.system.mapper.UserMapper;
import com.freehire.modules.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户服务实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final SubscriptionService subscriptionService;

    @Override
    public IPage<User> getUserPage(UserQuery query) {
        Page<User> page = new Page<>(query.getCurrent(), query.getSize());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getUsername()), User::getUsername, query.getUsername())
               .like(StringUtils.hasText(query.getRealName()), User::getRealName, query.getRealName())
               .like(StringUtils.hasText(query.getPhone()), User::getPhone, query.getPhone())
               .eq(query.getDeptId() != null, User::getDeptId, query.getDeptId())
               .eq(query.getStatus() != null, User::getStatus, query.getStatus())
               .orderByDesc(User::getCreateTime);

        return userMapper.selectPage(page, wrapper);
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    @Transactional
    public Long createUser(UserDTO dto) {
        // 检查用量限制
        if (!subscriptionService.checkQuota("user")) {
            throw new BusinessException("用户数已达套餐上限，请升级套餐");
        }

        // 检查用户名是否已存在
        User existing = userMapper.selectByUsername(dto.getUsername());
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        BeanUtil.copyProperties(dto, user);

        // 加密密码
        String password = StringUtils.hasText(dto.getPassword()) ? dto.getPassword() : "123456";
        user.setPassword(BCrypt.hashpw(password));

        if (StpUtil.isLogin()) {
            user.setCreateBy(StpUtil.getLoginIdAsLong());
        }

        userMapper.insert(user);

        // 分配角色
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), dto.getRoleIds());
        }

        // 更新用量
        subscriptionService.incrementUsage("user", 1);

        log.info("创建用户成功: {}", user.getId());
        return user.getId();
    }

    @Override
    @Transactional
    public void updateUser(UserDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }

        User existing = userMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户名是否被其他用户使用
        if (StringUtils.hasText(dto.getUsername()) && !dto.getUsername().equals(existing.getUsername())) {
            User byUsername = userMapper.selectByUsername(dto.getUsername());
            if (byUsername != null) {
                throw new BusinessException("用户名已存在");
            }
        }

        User user = new User();
        BeanUtil.copyProperties(dto, user);
        user.setPassword(null); // 更新时不修改密码

        if (StpUtil.isLogin()) {
            user.setUpdateBy(StpUtil.getLoginIdAsLong());
        }

        userMapper.updateById(user);

        // 更新角色
        if (dto.getRoleIds() != null) {
            assignRoles(dto.getId(), dto.getRoleIds());
        }

        log.info("更新用户成功: {}", dto.getId());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 不能删除admin
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不能删除管理员账号");
        }

        userMapper.deleteById(id);

        // 删除用户角色关联
        roleMapper.deleteUserRoleByUserId(id);

        // 更新用量
        subscriptionService.incrementUsage("user", -1);

        log.info("删除用户成功: {}", id);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        User update = new User();
        update.setId(id);
        update.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(update);

        log.info("重置密码成功: {}", id);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        User update = new User();
        update.setId(id);
        update.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(update);

        log.info("修改密码成功: {}", id);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 先删除原有角色
        roleMapper.deleteUserRoleByUserId(userId);

        // 再添加新角色
        for (Long roleId : roleIds) {
            roleMapper.insertUserRole(userId, roleId);
        }
    }
}

