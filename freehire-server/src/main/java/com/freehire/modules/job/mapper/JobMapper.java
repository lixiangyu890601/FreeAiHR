package com.freehire.modules.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freehire.modules.job.dto.JobQuery;
import com.freehire.modules.job.entity.Job;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 职位Mapper
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {

    /**
     * 分页查询职位（带部门名称等关联信息）
     */
    IPage<Job> selectJobPage(Page<Job> page, @Param("query") JobQuery query);

    /**
     * 统计在招职位数量
     */
    @Select("SELECT COUNT(*) FROM job_position WHERE status = 1 AND deleted = 0")
    int countActiveJobs();

    /**
     * 统计所有职位数量
     */
    @Select("SELECT COUNT(*) FROM job_position WHERE deleted = 0")
    int countJobs();

    /**
     * 统计开放中的职位数量（同 countActiveJobs）
     */
    @Select("SELECT COUNT(*) FROM job_position WHERE status = 1 AND deleted = 0")
    int countOpenJobs();
}

