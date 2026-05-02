package com.freehire.modules.system.service.impl;

import com.freehire.common.exception.BusinessException;
import com.freehire.modules.system.entity.Menu;
import com.freehire.modules.system.mapper.MenuMapper;
import com.freehire.modules.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public List<Menu> getMenuTree() {
        List<Menu> allMenus = menuMapper.selectAllMenus();
        return buildTree(allMenus, 0L);
    }

    @Override
    public List<Menu> getUserMenuTree(Long userId) {
        List<Menu> userMenus = menuMapper.selectByUserId(userId);
        // 只保留目录(M)和菜单(C)，过滤按钮(F)
        userMenus = userMenus.stream()
                .filter(m -> m.getType() != null && !"F".equals(m.getType()))
                .collect(Collectors.toList());
        return buildTree(userMenus, 0L);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        return menuMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public Menu getMenuById(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        return menu;
    }

    private List<Menu> buildTree(List<Menu> allMenus, Long parentId) {
        Map<Long, List<Menu>> parentMap = allMenus.stream()
                .collect(Collectors.groupingBy(Menu::getParentId));

        List<Menu> roots = parentMap.getOrDefault(parentId, new ArrayList<>());
        for (Menu menu : roots) {
            menu.setChildren(buildTree(allMenus, menu.getId()));
        }
        return roots;
    }
}

