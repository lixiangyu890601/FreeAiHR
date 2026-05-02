package com.freehire.modules.interview.manager;

import com.freehire.common.exception.BusinessException;
import com.freehire.common.manager.AIManager;
import com.freehire.common.manager.QuotaManager;
import com.freehire.modules.candidate.entity.Application;
import com.freehire.modules.candidate.entity.Candidate;
import com.freehire.modules.candidate.mapper.ApplicationMapper;
import com.freehire.modules.candidate.mapper.CandidateMapper;
import com.freehire.modules.interview.entity.Interview;
import com.freehire.modules.interview.mapper.InterviewMapper;
import com.freehire.modules.job.entity.Job;
import com.freehire.modules.job.mapper.JobMapper;
import com.freehire.modules.resume.entity.Resume;
import com.freehire.modules.resume.mapper.ResumeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 面试业务管理器
 * 处理面试流程、AI问题生成等复杂业务逻辑
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InterviewManager {

    private final InterviewMapper interviewMapper;
    private final ApplicationMapper applicationMapper;
    private final CandidateMapper candidateMapper;
    private final JobMapper jobMapper;
    private final ResumeMapper resumeMapper;
    private final AIManager aiManager;
    private final QuotaManager quotaManager;

    /**
     * 生成面试问题
     *
     * @param interviewId 面试ID
     * @return 面试问题列表
     */
    public List<String> generateInterviewQuestions(Long interviewId) {
        // 检查AI功能权限
        quotaManager.requireFeature("ai_generate_jd", "当前套餐不支持AI生成面试问题，请升级套餐");

        Interview interview = interviewMapper.selectById(interviewId);
        if (interview == null) {
            throw new BusinessException("面试不存在");
        }

        // 获取职位信息
        Job job = jobMapper.selectById(interview.getJobId());
        String jobTitle = job != null ? job.getTitle() : "";
        String jobDescription = job != null ? job.getDescription() : "";

        // 获取候选人简历文本
        String resumeText = getResumeText(interview);

        // 如果没有足够信息，返回空
        if (!StringUtils.hasText(jobTitle) && !StringUtils.hasText(resumeText)) {
            log.warn("无法生成面试问题：缺少职位或候选人信息");
            return Collections.emptyList();
        }

        // 调用AI生成面试问题
        return aiManager.generateInterviewQuestions(
                jobTitle + (StringUtils.hasText(jobDescription) ? "\n" + jobDescription : ""),
                resumeText
        );
    }

    /**
     * 获取面试相关的简历文本
     */
    private String getResumeText(Interview interview) {
        // 尝试从申请中获取简历
        Application application = applicationMapper.selectById(interview.getApplicationId());
        if (application != null && application.getResumeId() != null) {
            Resume resume = resumeMapper.selectById(application.getResumeId());
            if (resume != null && StringUtils.hasText(resume.getRawText())) {
                return resume.getRawText();
            }
        }

        // 尝试从候选人信息构建
        Candidate candidate = candidateMapper.selectById(interview.getCandidateId());
        if (candidate != null) {
            return buildCandidateProfile(candidate);
        }

        return "";
    }

    /**
     * 构建候选人简介
     */
    private String buildCandidateProfile(Candidate candidate) {
        StringBuilder sb = new StringBuilder();
        sb.append("候选人：").append(candidate.getName());
        
        if (StringUtils.hasText(candidate.getEducation())) {
            sb.append("，学历：").append(candidate.getEducation());
        }
        if (candidate.getWorkYears() != null) {
            sb.append("，工作年限：").append(candidate.getWorkYears()).append("年");
        }
        if (StringUtils.hasText(candidate.getCurrentPosition())) {
            sb.append("，当前职位：").append(candidate.getCurrentPosition());
        }
        if (StringUtils.hasText(candidate.getCurrentCompany())) {
            sb.append("，当前公司：").append(candidate.getCurrentCompany());
        }
        if (StringUtils.hasText(candidate.getSkills())) {
            sb.append("，技能：").append(candidate.getSkills());
        }
        
        return sb.toString();
    }

    /**
     * 填充面试关联信息
     *
     * @param interview 面试实体
     */
    public void fillInterviewInfo(Interview interview) {
        // 填充候选人信息
        Candidate candidate = candidateMapper.selectById(interview.getCandidateId());
        if (candidate != null) {
            interview.setCandidateName(candidate.getName());
        }

        // 填充职位信息
        Job job = jobMapper.selectById(interview.getJobId());
        if (job != null) {
            interview.setJobTitle(job.getTitle());
        }
    }

    /**
     * 批量填充面试关联信息
     *
     * @param interviews 面试列表
     */
    public void fillInterviewInfoBatch(List<Interview> interviews) {
        interviews.forEach(this::fillInterviewInfo);
    }
}

