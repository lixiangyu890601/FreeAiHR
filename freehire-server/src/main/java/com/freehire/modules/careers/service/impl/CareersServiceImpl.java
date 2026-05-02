package com.freehire.modules.careers.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freehire.common.exception.BusinessException;
import com.freehire.modules.candidate.entity.Application;
import com.freehire.modules.candidate.entity.Candidate;
import com.freehire.modules.candidate.mapper.ApplicationMapper;
import com.freehire.modules.candidate.mapper.CandidateMapper;
import com.freehire.modules.careers.dto.ApplyDTO;
import com.freehire.modules.careers.dto.CompanyInfoVO;
import com.freehire.modules.careers.dto.JobVO;
import com.freehire.modules.careers.service.CareersService;
import com.freehire.modules.job.entity.Job;
import com.freehire.modules.job.mapper.JobMapper;
import com.freehire.modules.resume.entity.Resume;
import com.freehire.modules.resume.mapper.ResumeMapper;
import com.freehire.modules.system.entity.Dept;
import com.freehire.modules.system.entity.User;
import com.freehire.modules.system.mapper.ConfigMapper;
import com.freehire.modules.system.mapper.DeptMapper;
import com.freehire.modules.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公开招聘服务实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CareersServiceImpl implements CareersService {

    private final JobMapper jobMapper;
    private final DeptMapper deptMapper;
    private final UserMapper userMapper;
    private final ConfigMapper configMapper;
    private final ResumeMapper resumeMapper;
    private final CandidateMapper candidateMapper;
    private final ApplicationMapper applicationMapper;

    @Override
    public CompanyInfoVO getCompanyInfo() {
        CompanyInfoVO vo = new CompanyInfoVO();
        
        // 从系统配置中获取公司信息
        vo.setName(getConfigValue("company_name", "FreeHire 公司"));
        vo.setLogo(getConfigValue("company_logo", ""));
        vo.setIntro(getConfigValue("company_intro", ""));
        vo.setContactEmail(getConfigValue("company_email", ""));
        vo.setAddress(getConfigValue("company_address", ""));
        vo.setScale(getConfigValue("company_scale", ""));
        vo.setIndustry(getConfigValue("company_industry", ""));
        vo.setWebsite(getConfigValue("company_website", ""));
        
        // 公司福利
        String benefits = getConfigValue("company_benefits", "[]");
        try {
            vo.setBenefits(JSONUtil.toList(benefits, String.class));
        } catch (Exception e) {
            vo.setBenefits(new ArrayList<>());
        }
        
        return vo;
    }

    @Override
    public List<JobVO> getJobList(String keyword, String city, String jobType) {
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getStatus, 1) // 只查询招聘中的职位
               .eq(Job::getDeleted, 0);
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Job::getTitle, keyword)
                    .or().like(Job::getDescription, keyword)
                    .or().like(Job::getRequirements, keyword));
        }
        
        if (StringUtils.hasText(city)) {
            wrapper.eq(Job::getCity, city);
        }
        
        if (StringUtils.hasText(jobType)) {
            wrapper.eq(Job::getJobType, jobType);
        }
        
        wrapper.orderByDesc(Job::getUrgent)
               .orderByDesc(Job::getPublishDate)
               .orderByDesc(Job::getCreateTime);
        
        List<Job> jobs = jobMapper.selectList(wrapper);
        return jobs.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public JobVO getJobDetail(Long jobId) {
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getDeleted() == 1) {
            throw new BusinessException("职位不存在");
        }
        if (job.getStatus() != 1) {
            throw new BusinessException("该职位已停止招聘");
        }
        
        // 增加浏览次数
        job.setViewCount(job.getViewCount() == null ? 1 : job.getViewCount() + 1);
        jobMapper.updateById(job);
        
        return convertToVO(job);
    }

    @Override
    @Transactional
    public Long apply(ApplyDTO dto) {
        // 1. 验证职位
        Job job = jobMapper.selectById(dto.getJobId());
        if (job == null || job.getDeleted() == 1 || job.getStatus() != 1) {
            throw new BusinessException("职位不存在或已关闭");
        }
        
        // 2. 检查是否已投递
        if (hasApplied(dto.getJobId(), dto.getPhone())) {
            throw new BusinessException("您已投递过该职位，请勿重复投递");
        }
        
        // 3. 查找或创建候选人
        Candidate candidate = findOrCreateCandidate(dto);
        
        // 4. 创建简历记录
        Resume resume = new Resume();
        resume.setCandidateId(candidate.getId());
        resume.setFileName(dto.getResumeFileName());
        resume.setFilePath(dto.getResumePath());
        resume.setFileType(getFileType(dto.getResumeFileName()));
        resume.setName(dto.getName());
        resume.setPhone(dto.getPhone());
        resume.setEmail(dto.getEmail());
        resume.setSource("website"); // 官网投递
        resume.setParsed(0);
        resume.setParseStatus("pending");
        resumeMapper.insert(resume);
        
        // 更新候选人的最新简历ID
        candidate.setLatestResumeId(resume.getId());
        candidateMapper.updateById(candidate);
        
        // 5. 创建申请记录
        Application application = new Application();
        application.setCandidateId(candidate.getId());
        application.setJobId(dto.getJobId());
        application.setResumeId(resume.getId());
        application.setStage("new");
        application.setSource("website"); // 官网投递
        application.setApplyTime(LocalDateTime.now());
        application.setStageUpdateTime(LocalDateTime.now());
        application.setHrUserId(job.getHrUserId());
        applicationMapper.insert(application);
        
        // 6. 更新职位投递数量
        job.setApplyCount(job.getApplyCount() == null ? 1 : job.getApplyCount() + 1);
        jobMapper.updateById(job);
        
        log.info("求职者投递成功: jobId={}, candidateId={}, applicationId={}", 
                dto.getJobId(), candidate.getId(), application.getId());
        
        return application.getId();
    }

    @Override
    public boolean hasApplied(Long jobId, String phone) {
        // 先查找候选人
        Candidate candidate = candidateMapper.selectOne(
                new LambdaQueryWrapper<Candidate>()
                        .eq(Candidate::getPhone, phone)
                        .eq(Candidate::getDeleted, 0));
        
        if (candidate == null) {
            return false;
        }
        
        // 检查是否有该职位的申请
        Long count = applicationMapper.selectCount(
                new LambdaQueryWrapper<Application>()
                        .eq(Application::getCandidateId, candidate.getId())
                        .eq(Application::getJobId, jobId));
        
        return count > 0;
    }

    @Override
    public List<String> getCityOptions() {
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Job::getStatus, 1)
               .eq(Job::getDeleted, 0)
               .isNotNull(Job::getCity)
               .select(Job::getCity)
               .groupBy(Job::getCity);
        
        return jobMapper.selectList(wrapper).stream()
                .map(Job::getCity)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private JobVO convertToVO(Job job) {
        JobVO vo = new JobVO();
        vo.setId(job.getId());
        vo.setTitle(job.getTitle());
        vo.setJobType(job.getJobType());
        vo.setJobTypeName(getJobTypeName(job.getJobType()));
        vo.setCity(job.getCity());
        vo.setAddress(job.getAddress());
        vo.setSalaryRange(formatSalaryRange(job.getSalaryMin(), job.getSalaryMax(), job.getSalaryMonth()));
        vo.setEducation(job.getEducation());
        vo.setExperience(job.getExperience());
        vo.setHeadcount(job.getHeadcount());
        vo.setDescription(job.getDescription());
        vo.setRequirements(job.getRequirements());
        vo.setHighlights(job.getHighlights());
        vo.setUrgent(job.getUrgent() != null && job.getUrgent() == 1);
        vo.setPublishDate(job.getPublishDate());
        
        // 部门名称
        if (job.getDeptId() != null) {
            Dept dept = deptMapper.selectById(job.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
        }
        
        // HR姓名
        if (job.getHrUserId() != null) {
            User hr = userMapper.selectById(job.getHrUserId());
            if (hr != null) {
                vo.setHrName(hr.getRealName());
            }
        }
        
        // 标签
        if (StringUtils.hasText(job.getTags())) {
            try {
                vo.setTags(JSONUtil.toList(job.getTags(), String.class));
            } catch (Exception e) {
                vo.setTags(new ArrayList<>());
            }
        } else {
            vo.setTags(new ArrayList<>());
        }
        
        return vo;
    }

    /**
     * 格式化薪资范围
     */
    private String formatSalaryRange(Integer min, Integer max, Integer month) {
        if (min == null && max == null) {
            return "面议";
        }
        
        StringBuilder sb = new StringBuilder();
        if (min != null && max != null) {
            sb.append(min).append("-").append(max).append("K");
        } else if (min != null) {
            sb.append(min).append("K起");
        } else {
            sb.append(max).append("K以内");
        }
        
        if (month != null && month > 12) {
            sb.append(" · ").append(month).append("薪");
        }
        
        return sb.toString();
    }

    /**
     * 获取职位类型名称
     */
    private String getJobTypeName(String jobType) {
        if (jobType == null) return "全职";
        return switch (jobType) {
            case "full_time" -> "全职";
            case "part_time" -> "兼职";
            case "intern" -> "实习";
            default -> "全职";
        };
    }

    /**
     * 获取配置值
     */
    private String getConfigValue(String key, String defaultValue) {
        String value = configMapper.getValueByKey(key);
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    /**
     * 查找或创建候选人
     */
    private Candidate findOrCreateCandidate(ApplyDTO dto) {
        // 先通过手机号查找
        Candidate candidate = candidateMapper.selectOne(
                new LambdaQueryWrapper<Candidate>()
                        .eq(Candidate::getPhone, dto.getPhone())
                        .eq(Candidate::getDeleted, 0));
        
        if (candidate != null) {
            // 更新信息
            if (StringUtils.hasText(dto.getName())) {
                candidate.setName(dto.getName());
            }
            if (StringUtils.hasText(dto.getEmail())) {
                candidate.setEmail(dto.getEmail());
            }
            candidateMapper.updateById(candidate);
            return candidate;
        }
        
        // 创建新候选人
        candidate = new Candidate();
        candidate.setName(dto.getName());
        candidate.setPhone(dto.getPhone());
        candidate.setEmail(dto.getEmail());
        candidate.setSource("website"); // 官网投递
        candidateMapper.insert(candidate);
        
        return candidate;
    }

    /**
     * 获取文件类型
     */
    private String getFileType(String fileName) {
        if (fileName == null) return null;
        int idx = fileName.lastIndexOf(".");
        if (idx >= 0) {
            return fileName.substring(idx + 1).toLowerCase();
        }
        return null;
    }
}

