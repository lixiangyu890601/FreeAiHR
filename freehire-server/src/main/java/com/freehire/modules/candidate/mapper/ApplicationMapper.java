package com.freehire.modules.candidate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freehire.modules.candidate.dto.ApplicationVO;
import com.freehire.modules.candidate.entity.Application;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 求职申请Mapper
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {

    /**
     * 分页查询申请（带候选人和职位信息）
     */
    IPage<ApplicationVO> selectApplicationPage(Page<ApplicationVO> page, @Param("query") Map<String, Object> query);

    /**
     * 统计各阶段申请数量
     */
    @Select("""
            SELECT stage, COUNT(*) as count 
            FROM application 
            WHERE deleted = 0 
            GROUP BY stage
            """)
    java.util.List<Map<String, Object>> countByStage();

    /**
     * 统计指定职位的各阶段数量
     */
    @Select("""
            SELECT stage, COUNT(*) as count 
            FROM application 
            WHERE job_id = #{jobId} AND deleted = 0 
            GROUP BY stage
            """)
    java.util.List<Map<String, Object>> countByStageForJob(@Param("jobId") Long jobId);

    /**
     * 统计各来源数量
     */
    @Select("""
            SELECT COALESCE(source, '其他') as source, COUNT(*) as count 
            FROM application 
            WHERE deleted = 0 
            GROUP BY source
            ORDER BY count DESC
            """)
    java.util.List<Map<String, Object>> countBySource();
}

