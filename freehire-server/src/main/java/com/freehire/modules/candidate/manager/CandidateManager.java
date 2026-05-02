package com.freehire.modules.candidate.manager;

import cn.dev33.satoken.stp.StpUtil;
import com.freehire.common.exception.BusinessException;
import com.freehire.common.manager.QuotaManager;
import com.freehire.common.response.ResultCode;
import com.freehire.modules.candidate.entity.Candidate;
import com.freehire.modules.candidate.mapper.CandidateMapper;
import com.freehire.modules.resume.entity.Resume;
import com.freehire.modules.resume.mapper.ResumeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 候选人业务管理器
 * 处理候选人创建、简历关联等复杂业务逻辑
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CandidateManager {

    private final CandidateMapper candidateMapper;
    private final ResumeMapper resumeMapper;
    private final QuotaManager quotaManager;

    /**
     * 从简历创建或更新候选人
     *
     * @param resumeId 简历ID
     * @return 候选人实体
     */
    @Transactional
    public Candidate createOrUpdateFromResume(Long resumeId) {
        Resume resume = resumeMapper.selectById(resumeId);
        if (resume == null) {
            throw new BusinessException(ResultCode.RESUME_NOT_EXIST);
        }

        // 检查是否已存在（根据手机号或邮箱）
        Candidate existing = findByPhoneOrEmail(resume.getPhone(), resume.getEmail());
        
        if (existing != null) {
            // 更新现有候选人的最新简历
            existing.setLatestResumeId(resumeId);
            candidateMapper.updateById(existing);
            
            // 更新简历的候选人ID
            resume.setCandidateId(existing.getId());
            resumeMapper.updateById(resume);
            
            log.info("更新候选人简历关联: candidateId={}, resumeId={}", existing.getId(), resumeId);
            return existing;
        }

        // 创建新候选人
        return createFromResume(resume);
    }

    /**
     * 从简历创建新候选人
     */
    private Candidate createFromResume(Resume resume) {
        Candidate candidate = new Candidate();
        
        // 复制基本信息
        candidate.setName(resume.getName());
        candidate.setPhone(resume.getPhone());
        candidate.setEmail(resume.getEmail());
        candidate.setGender(parseGender(resume.getGender()));
        candidate.setBirthDate(resume.getBirthDate());
        candidate.setAge(resume.getAge());
        candidate.setCity(resume.getCity());
        candidate.setEducation(resume.getEducation());
        candidate.setSchool(resume.getSchool());
        candidate.setMajor(resume.getMajor());
        candidate.setWorkYears(resume.getWorkYears());
        candidate.setCurrentCompany(resume.getCurrentCompany());
        candidate.setCurrentPosition(resume.getCurrentPosition());
        candidate.setExpectPosition(resume.getExpectPosition());
        candidate.setExpectCity(resume.getExpectCity());
        candidate.setExpectSalary(resume.getExpectSalary());
        candidate.setSkills(resume.getSkills());
        candidate.setSource(resume.getSource());
        candidate.setSourceDetail(resume.getSourceDetail());
        candidate.setLatestResumeId(resume.getId());

        if (StpUtil.isLogin()) {
            candidate.setCreateBy(StpUtil.getLoginIdAsLong());
        }

        candidateMapper.insert(candidate);

        // 更新简历的候选人ID
        resume.setCandidateId(candidate.getId());
        resumeMapper.updateById(resume);

        // 更新用量统计
        quotaManager.incrementUsage("candidate", 1);

        log.info("从简历创建候选人成功: candidateId={}, resumeId={}", candidate.getId(), resume.getId());
        return candidate;
    }

    /**
     * 根据手机号或邮箱查找候选人
     */
    public Candidate findByPhoneOrEmail(String phone, String email) {
        if (!StringUtils.hasText(phone) && !StringUtils.hasText(email)) {
            return null;
        }

        // 优先按手机号查找
        if (StringUtils.hasText(phone)) {
            Candidate byPhone = candidateMapper.selectByPhone(phone);
            if (byPhone != null) {
                return byPhone;
            }
        }

        // 再按邮箱查找
        if (StringUtils.hasText(email)) {
            return candidateMapper.selectByEmail(email);
        }

        return null;
    }

    /**
     * 解析性别
     */
    private Integer parseGender(String gender) {
        if (!StringUtils.hasText(gender)) {
            return 0;
        }
        return switch (gender) {
            case "男" -> 1;
            case "女" -> 2;
            default -> 0;
        };
    }
}

