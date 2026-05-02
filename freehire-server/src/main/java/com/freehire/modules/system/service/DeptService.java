package com.freehire.modules.system.service;

import com.freehire.modules.system.dto.DeptDTO;
import com.freehire.modules.system.entity.Dept;

import java.util.List;

/**
 * 部门服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface DeptService {

    /**
     * 获取部门树
     */
    List<Dept> getDeptTree();

    /**
     * 获取部门列表
     */
    List<Dept> getDeptList();

    /**
     * 获取部门详情
     */
    Dept getDeptById(Long id);

    /**
     * 新增部门
     */
    Long createDept(DeptDTO dto);

    /**
     * 更新部门
     */
    void updateDept(DeptDTO dto);

    /**
     * 删除部门
     */
    void deleteDept(Long id);
}

