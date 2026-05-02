package com.freehire.modules.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.modules.job.dto.JobDTO;
import com.freehire.modules.job.dto.JobQuery;
import com.freehire.modules.job.entity.Job;

/**
 * 职位服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface JobService {

    /**
     * 分页查询职位
     */
    IPage<Job> getJobPage(JobQuery query);

    /**
     * 获取职位详情
     */
    Job getJobById(Long id);

    /**
     * 新增职位
     */
    Long createJob(JobDTO dto);

    /**
     * 更新职位
     */
    void updateJob(JobDTO dto);

    /**
     * 删除职位
     */
    void deleteJob(Long id);

    /**
     * 发布职位（状态改为招聘中）
     */
    void publishJob(Long id);

    /**
     * 关闭职位
     */
    void closeJob(Long id);

    /**
     * 暂停职位
     */
    void pauseJob(Long id);

    /**
     * 统计在招职位数量
     */
    int countActiveJobs();
}

