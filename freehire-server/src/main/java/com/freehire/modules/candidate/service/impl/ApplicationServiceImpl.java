package com.freehire.modules.candidate.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freehire.common.exception.BusinessException;
import com.freehire.common.response.ResultCode;
import com.freehire.modules.ai.dto.MatchResult;
import com.freehire.modules.ai.service.AIService;
import com.freehire.modules.candidate.dto.*;
import com.freehire.modules.candidate.entity.Application;
import com.freehire.modules.candidate.entity.Candidate;
import com.freehire.modules.candidate.mapper.ApplicationMapper;
import com.freehire.modules.candidate.mapper.CandidateMapper;
import com.freehire.modules.candidate.service.ApplicationService;
import com.freehire.modules.job.entity.Job;
import com.freehire.modules.job.mapper.JobMapper;
import com.freehire.modules.resume.entity.Resume;
import com.freehire.modules.resume.mapper.ResumeMapper;
import com.freehire.modules.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 求职申请服务实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationMapper applicationMapper;
    private final CandidateMapper candidateMapper;
    private final JobMapper jobMapper;
    private final ResumeMapper resumeMapper;
    private final AIService aiService;
    private final SubscriptionService subscriptionService;

    // 招聘阶段定义
    private static final List<String> STAGES = Arrays.asList(
            "new",              // 新投递
            "filtered",         // 简历筛选通过
            "interview_pending", // 待面试
            "interviewing",     // 面试中
            "interview_passed", // 面试通过
            "offer_pending",    // 待发Offer
            "offered",          // 已发Offer
            "onboarded",        // 已入职
            "rejected",         // 已淘汰
            "withdrawn"         // 已撤回
    );

    @Override
    public IPage<ApplicationVO> getApplicationPage(ApplicationQuery query) {
        Page<ApplicationVO> page = new Page<>(query.getCurrent(), query.getSize());

        // 构建查询条件
        Map<String, Object> params = new HashMap<>();
        params.put("candidateId", query.getCandidateId());
        params.put("jobId", query.getJobId());
        params.put("stage", query.getStage());
        params.put("candidateName", query.getCandidateName());
        params.put("jobTitle", query.getJobTitle());
        params.put("source", query.getSource());

        // 使用简单查询（不走自定义XML）
        Page<Application> appPage = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getCandidateId() != null, Application::getCandidateId, query.getCandidateId())
               .eq(query.getJobId() != null, Application::getJobId, query.getJobId())
               .eq(StringUtils.hasText(query.getStage()), Application::getStage, query.getStage())
               .eq(StringUtils.hasText(query.getSource()), Application::getSource, query.getSource())
               .orderByDesc(Application::getCreateTime);

        IPage<Application> result = applicationMapper.selectPage(appPage, wrapper);

        // 转换为VO
        List<ApplicationVO> voList = result.getRecords().stream().map(app -> {
            ApplicationVO vo = new ApplicationVO();
            BeanUtil.copyProperties(app, vo);

            // 填充候选人信息
            Candidate candidate = candidateMapper.selectById(app.getCandidateId());
            if (candidate != null) {
                vo.setCandidateName(candidate.getName());
                vo.setCandidatePhone(candidate.getPhone());
                vo.setCandidateEmail(candidate.getEmail());
                vo.setCandidateEducation(candidate.getEducation());
                vo.setCandidateWorkYears(candidate.getWorkYears());
                vo.setCandidateCurrentCompany(candidate.getCurrentCompany());
                vo.setCandidateCurrentPosition(candidate.getCurrentPosition());
            }

            // 填充职位信息
            Job job = jobMapper.selectById(app.getJobId());
            if (job != null) {
                vo.setJobTitle(job.getTitle());
                vo.setJobCity(job.getCity());
            }

            return vo;
        }).collect(Collectors.toList());

        // 构建返回结果
        Page<ApplicationVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Application getApplicationById(Long id) {
        Application application = applicationMapper.selectById(id);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        return application;
    }

    @Override
    @Transactional
    public Long createApplication(ApplicationDTO dto) {
        // 检查候选人
        Candidate candidate = candidateMapper.selectById(dto.getCandidateId());
        if (candidate == null) {
            throw new BusinessException(ResultCode.CANDIDATE_NOT_EXIST);
        }

        // 检查职位
        Job job = jobMapper.selectById(dto.getJobId());
        if (job == null) {
            throw new BusinessException(ResultCode.JOB_NOT_EXIST);
        }

        // 检查是否已申请过
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getCandidateId, dto.getCandidateId())
               .eq(Application::getJobId, dto.getJobId())
               .notIn(Application::getStage, Arrays.asList("rejected", "withdrawn"));
        if (applicationMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该候选人已申请过此职位");
        }

        Application application = new Application();
        application.setCandidateId(dto.getCandidateId());
        application.setJobId(dto.getJobId());
        application.setResumeId(dto.getResumeId() != null ? dto.getResumeId() : candidate.getLatestResumeId());
        application.setStage("new");
        application.setSource(dto.getSource());
        application.setRemark(dto.getRemark());
        application.setApplyTime(LocalDateTime.now());

        if (StpUtil.isLogin()) {
            application.setCreateBy(StpUtil.getLoginIdAsLong());
            application.setHrUserId(StpUtil.getLoginIdAsLong());
        }

        applicationMapper.insert(application);

        // 更新职位投递数
        job.setApplyCount(job.getApplyCount() + 1);
        jobMapper.updateById(job);

        log.info("创建申请成功: {}", application.getId());
        return application.getId();
    }

    @Override
    @Transactional
    public void updateApplication(ApplicationDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("申请ID不能为空");
        }

        Application existing = applicationMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("申请不存在");
        }

        Application application = new Application();
        application.setId(dto.getId());
        application.setResumeId(dto.getResumeId());
        application.setRemark(dto.getRemark());

        if (StpUtil.isLogin()) {
            application.setUpdateBy(StpUtil.getLoginIdAsLong());
        }

        applicationMapper.updateById(application);
        log.info("更新申请成功: {}", dto.getId());
    }

    @Override
    @Transactional
    public void deleteApplication(Long id) {
        Application application = applicationMapper.selectById(id);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }

        applicationMapper.deleteById(id);
        log.info("删除申请成功: {}", id);
    }

    @Override
    @Transactional
    public void changeStage(StageChangeDTO dto) {
        Application application = applicationMapper.selectById(dto.getApplicationId());
        if (application == null) {
            throw new BusinessException("申请不存在");
        }

        // 验证阶段
        if (!STAGES.contains(dto.getTargetStage())) {
            throw new BusinessException("无效的阶段: " + dto.getTargetStage());
        }

        // 淘汰需要原因
        if ("rejected".equals(dto.getTargetStage()) && !StringUtils.hasText(dto.getRejectReason())) {
            throw new BusinessException("淘汰原因不能为空");
        }

        application.setStage(dto.getTargetStage());
        application.setStageUpdateTime(LocalDateTime.now());
        if (StringUtils.hasText(dto.getRejectReason())) {
            application.setRejectReason(dto.getRejectReason());
        }
        if (StringUtils.hasText(dto.getRemark())) {
            application.setRemark(dto.getRemark());
        }

        if (StpUtil.isLogin()) {
            application.setUpdateBy(StpUtil.getLoginIdAsLong());
        }

        applicationMapper.updateById(application);
        log.info("变更阶段成功: applicationId={}, stage={}", dto.getApplicationId(), dto.getTargetStage());
    }

    @Override
    @Transactional
    public void batchChangeStage(List<Long> applicationIds, String targetStage, String reason) {
        for (Long id : applicationIds) {
            StageChangeDTO dto = new StageChangeDTO();
            dto.setApplicationId(id);
            dto.setTargetStage(targetStage);
            dto.setRejectReason(reason);
            changeStage(dto);
        }
    }

    @Override
    @Transactional
    public void aiMatch(Long applicationId) {
        // 检查AI匹配功能权限
        if (!subscriptionService.hasFeature("ai_match")) {
            throw new BusinessException(ResultCode.FEATURE_NOT_AVAILABLE.getCode(),
                    "当前套餐不支持AI智能匹配功能，请升级套餐");
        }

        // 检查用量
        if (!subscriptionService.checkQuota("ai_match")) {
            throw new BusinessException(ResultCode.QUOTA_EXCEEDED.getCode(),
                    "本月AI匹配次数已达上限，请升级套餐");
        }

        Application application = applicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }

        // 获取简历内容
        Resume resume = null;
        if (application.getResumeId() != null) {
            resume = resumeMapper.selectById(application.getResumeId());
        }
        String resumeText = resume != null && StringUtils.hasText(resume.getRawText()) 
                ? resume.getRawText() 
                : buildResumeTextFromCandidate(application.getCandidateId());

        // 获取职位描述
        Job job = jobMapper.selectById(application.getJobId());
        String jobDescription = buildJobDescription(job);

        // 调用AI匹配
        MatchResult result = aiService.matchResumeWithJob(resumeText, jobDescription);

        // 更新匹配结果
        application.setMatchScore(result.getOverallScore());
        application.setMatchAnalysis(result.getSummary());
        applicationMapper.updateById(application);

        // 更新用量统计
        subscriptionService.incrementUsage("ai_match", 1);

        log.info("AI匹配完成: applicationId={}, score={}", applicationId, result.getOverallScore());
    }

    @Override
    public List<Application> getApplicationsByCandidateId(Long candidateId) {
        return applicationMapper.selectList(new LambdaQueryWrapper<Application>()
                .eq(Application::getCandidateId, candidateId)
                .orderByDesc(Application::getCreateTime));
    }

    @Override
    public List<Application> getApplicationsByJobId(Long jobId) {
        return applicationMapper.selectList(new LambdaQueryWrapper<Application>()
                .eq(Application::getJobId, jobId)
                .orderByDesc(Application::getCreateTime));
    }

    @Override
    public Map<String, Long> countByStage() {
        List<Map<String, Object>> result = applicationMapper.countByStage();
        Map<String, Long> countMap = new HashMap<>();
        for (Map<String, Object> item : result) {
            countMap.put((String) item.get("stage"), ((Number) item.get("count")).longValue());
        }
        return countMap;
    }

    @Override
    public List<Map<String, Object>> getFunnelData(Long jobId) {
        List<Map<String, Object>> result;
        if (jobId != null) {
            result = applicationMapper.countByStageForJob(jobId);
        } else {
            result = applicationMapper.countByStage();
        }

        // 按漏斗顺序整理
        List<String> funnelStages = Arrays.asList(
                "new", "filtered", "interview_pending", "interviewing",
                "interview_passed", "offer_pending", "offered", "onboarded"
        );

        Map<String, Long> countMap = new HashMap<>();
        for (Map<String, Object> item : result) {
            countMap.put((String) item.get("stage"), ((Number) item.get("count")).longValue());
        }

        List<Map<String, Object>> funnel = new ArrayList<>();
        for (String stage : funnelStages) {
            Map<String, Object> item = new HashMap<>();
            item.put("stage", stage);
            item.put("stageName", getStageName(stage));
            item.put("count", countMap.getOrDefault(stage, 0L));
            funnel.add(item);
        }

        return funnel;
    }

    private String buildResumeTextFromCandidate(Long candidateId) {
        Candidate candidate = candidateMapper.selectById(candidateId);
        if (candidate == null) {
            return "";
        }
        return String.format("""
                姓名：%s
                学历：%s
                院校：%s
                专业：%s
                工作年限：%d年
                当前公司：%s
                当前职位：%s
                期望职位：%s
                技能：%s
                """,
                candidate.getName(),
                candidate.getEducation(),
                candidate.getSchool(),
                candidate.getMajor(),
                candidate.getWorkYears(),
                candidate.getCurrentCompany(),
                candidate.getCurrentPosition(),
                candidate.getExpectPosition(),
                candidate.getSkills()
        );
    }

    private String buildJobDescription(Job job) {
        if (job == null) {
            return "";
        }
        return String.format("""
                职位名称：%s
                工作城市：%s
                学历要求：%s
                经验要求：%s
                薪资范围：%d-%dK
                职位描述：%s
                任职要求：%s
                """,
                job.getTitle(),
                job.getCity(),
                job.getEducation(),
                job.getExperience(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getDescription(),
                job.getRequirements()
        );
    }

    private String getStageName(String stage) {
        return switch (stage) {
            case "new" -> "新投递";
            case "filtered" -> "简历筛选";
            case "interview_pending" -> "待面试";
            case "interviewing" -> "面试中";
            case "interview_passed" -> "面试通过";
            case "offer_pending" -> "待发Offer";
            case "offered" -> "已发Offer";
            case "onboarded" -> "已入职";
            case "rejected" -> "已淘汰";
            case "withdrawn" -> "已撤回";
            default -> stage;
        };
    }

    @Override
    @Transactional
    public Long recommendToJob(Long candidateId, Long jobId) {
        // 验证候选人存在
        Candidate candidate = candidateMapper.selectById(candidateId);
        if (candidate == null) {
            throw new BusinessException("候选人不存在");
        }

        // 验证职位存在
        Job job = jobMapper.selectById(jobId);
        if (job == null) {
            throw new BusinessException("职位不存在");
        }

        // 检查是否已推荐过
        Long existCount = applicationMapper.selectCount(new LambdaQueryWrapper<Application>()
                .eq(Application::getCandidateId, candidateId)
                .eq(Application::getJobId, jobId));
        if (existCount > 0) {
            throw new BusinessException("该候选人已被推荐到此职位");
        }

        // 创建申请记录
        Application application = new Application();
        application.setCandidateId(candidateId);
        application.setJobId(jobId);
        application.setResumeId(candidate.getLatestResumeId());
        application.setStage("new");
        application.setSource("recommend");  // 来源标记为推荐
        application.setApplyTime(LocalDateTime.now());
        application.setStageUpdateTime(LocalDateTime.now());

        if (StpUtil.isLogin()) {
            application.setHrUserId(StpUtil.getLoginIdAsLong());
            application.setCreateBy(StpUtil.getLoginIdAsLong());
        }

        applicationMapper.insert(application);
        log.info("推荐候选人到职位: candidateId={}, jobId={}, applicationId={}", 
                candidateId, jobId, application.getId());

        return application.getId();
    }
}

