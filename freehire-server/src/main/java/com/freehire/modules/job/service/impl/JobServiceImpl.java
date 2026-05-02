package com.freehire.modules.job.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freehire.common.exception.BusinessException;
import com.freehire.common.manager.QuotaManager;
import com.freehire.common.response.ResultCode;
import com.freehire.modules.job.dto.JobDTO;
import com.freehire.modules.job.dto.JobQuery;
import com.freehire.modules.job.entity.Job;
import com.freehire.modules.job.mapper.JobMapper;
import com.freehire.modules.job.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * 职位服务实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobMapper jobMapper;
    private final QuotaManager quotaManager;

    @Override
    public IPage<Job> getJobPage(JobQuery query) {
        Page<Job> page = new Page<>(query.getCurrent(), query.getSize());
        
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getTitle()), Job::getTitle, query.getTitle())
               .eq(query.getDeptId() != null, Job::getDeptId, query.getDeptId())
               .eq(StringUtils.hasText(query.getCity()), Job::getCity, query.getCity())
               .eq(query.getStatus() != null, Job::getStatus, query.getStatus())
               .eq(query.getUrgent() != null, Job::getUrgent, query.getUrgent())
               .orderByDesc(Job::getCreateTime);
        
        IPage<Job> result = jobMapper.selectPage(page, wrapper);
        
        // 处理标签
        result.getRecords().forEach(job -> {
            if (StringUtils.hasText(job.getTags())) {
                job.setTagList(JSONUtil.toList(job.getTags(), String.class));
            }
        });
        
        return result;
    }

    @Override
    public Job getJobById(Long id) {
        Job job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException(ResultCode.JOB_NOT_EXIST);
        }
        if (StringUtils.hasText(job.getTags())) {
            job.setTagList(JSONUtil.toList(job.getTags(), String.class));
        }
        return job;
    }

    @Override
    @Transactional
    public Long createJob(JobDTO dto) {
        // 检查用量
        quotaManager.requireQuota("job", "职位数量已达上限，请升级套餐");

        Job job = new Job();
        BeanUtil.copyProperties(dto, job);
        
        // 处理标签
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            job.setTags(JSONUtil.toJsonStr(dto.getTags()));
        }
        
        // 设置默认值
        job.setStatus(0); // 新建时为草稿状态
        job.setViewCount(0);
        job.setApplyCount(0);
        
        // 设置创建人
        if (StpUtil.isLogin()) {
            job.setCreateBy(StpUtil.getLoginIdAsLong());
            if (job.getHrUserId() == null) {
                job.setHrUserId(StpUtil.getLoginIdAsLong());
            }
        }
        
        jobMapper.insert(job);
        
        // 更新用量统计
        quotaManager.incrementUsage("job", 1);
        
        log.info("创建职位成功: {}", job.getId());
        return job.getId();
    }

    @Override
    @Transactional
    public void updateJob(JobDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("职位ID不能为空");
        }
        
        Job existing = jobMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.JOB_NOT_EXIST);
        }
        
        Job job = new Job();
        BeanUtil.copyProperties(dto, job);
        
        // 处理标签
        if (dto.getTags() != null) {
            job.setTags(JSONUtil.toJsonStr(dto.getTags()));
        }
        
        // 设置更新人
        if (StpUtil.isLogin()) {
            job.setUpdateBy(StpUtil.getLoginIdAsLong());
        }
        
        jobMapper.updateById(job);
        log.info("更新职位成功: {}", dto.getId());
    }

    @Override
    @Transactional
    public void deleteJob(Long id) {
        Job job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException(ResultCode.JOB_NOT_EXIST);
        }
        
        jobMapper.deleteById(id);
        
        // 减少用量统计
        quotaManager.incrementUsage("job", -1);
        
        log.info("删除职位成功: {}", id);
    }

    @Override
    @Transactional
    public void publishJob(Long id) {
        Job job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException(ResultCode.JOB_NOT_EXIST);
        }
        
        job.setStatus(1); // 招聘中
        job.setPublishDate(LocalDate.now());
        jobMapper.updateById(job);
        
        log.info("发布职位成功: {}", id);
    }

    @Override
    @Transactional
    public void closeJob(Long id) {
        Job job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException(ResultCode.JOB_NOT_EXIST);
        }
        
        job.setStatus(0); // 关闭
        jobMapper.updateById(job);
        
        log.info("关闭职位成功: {}", id);
    }

    @Override
    @Transactional
    public void pauseJob(Long id) {
        Job job = jobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException(ResultCode.JOB_NOT_EXIST);
        }
        
        job.setStatus(2); // 暂停
        jobMapper.updateById(job);
        
        log.info("暂停职位成功: {}", id);
    }

    @Override
    public int countActiveJobs() {
        return jobMapper.countActiveJobs();
    }
}
