package com.freehire.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freehire.modules.system.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部门Mapper
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    /**
     * 查询子部门
     */
    @Select("SELECT * FROM sys_dept WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort")
    List<Dept> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有部门（树形结构用）
     */
    @Select("SELECT * FROM sys_dept WHERE deleted = 0 ORDER BY sort")
    List<Dept> selectAllDepts();
}

