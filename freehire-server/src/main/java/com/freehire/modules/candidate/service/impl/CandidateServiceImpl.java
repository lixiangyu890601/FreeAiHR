package com.freehire.modules.candidate.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freehire.common.exception.BusinessException;
import com.freehire.common.manager.QuotaManager;
import com.freehire.common.response.ResultCode;
import com.freehire.modules.candidate.dto.CandidateDTO;
import com.freehire.modules.candidate.dto.CandidateQuery;
import com.freehire.modules.candidate.entity.Candidate;
import com.freehire.modules.candidate.manager.CandidateManager;
import com.freehire.modules.candidate.mapper.CandidateMapper;
import com.freehire.modules.candidate.service.CandidateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 候选人服务实现
 * 简化版：复杂业务逻辑委托给 CandidateManager
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateMapper candidateMapper;
    private final CandidateManager candidateManager;
    private final QuotaManager quotaManager;

    @Override
    public IPage<Candidate> getCandidatePage(CandidateQuery query) {
        Page<Candidate> page = new Page<>(query.getCurrent(), query.getSize());

        LambdaQueryWrapper<Candidate> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getName()), Candidate::getName, query.getName())
               .like(StringUtils.hasText(query.getPhone()), Candidate::getPhone, query.getPhone())
               .like(StringUtils.hasText(query.getEmail()), Candidate::getEmail, query.getEmail())
               .eq(StringUtils.hasText(query.getEducation()), Candidate::getEducation, query.getEducation())
               .eq(StringUtils.hasText(query.getSource()), Candidate::getSource, query.getSource())
               .like(StringUtils.hasText(query.getTags()), Candidate::getTags, query.getTags())
               .orderByDesc(Candidate::getCreateTime);

        return candidateMapper.selectPage(page, wrapper);
    }

    @Override
    public Candidate getCandidateById(Long id) {
        Candidate candidate = candidateMapper.selectById(id);
        if (candidate == null) {
            throw new BusinessException(ResultCode.CANDIDATE_NOT_EXIST);
        }
        return candidate;
    }

    @Override
    @Transactional
    public Long createCandidate(CandidateDTO dto) {
        // 检查是否已存在
        Candidate existing = candidateManager.findByPhoneOrEmail(dto.getPhone(), dto.getEmail());
        if (existing != null) {
            throw new BusinessException("候选人已存在（手机号或邮箱重复）");
        }

        Candidate candidate = new Candidate();
        BeanUtil.copyProperties(dto, candidate);

        // 处理技能和标签
        if (dto.getSkills() != null && !dto.getSkills().isEmpty()) {
            candidate.setSkills(JSONUtil.toJsonStr(dto.getSkills()));
        }
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            candidate.setTags(JSONUtil.toJsonStr(dto.getTags()));
        }

        if (StpUtil.isLogin()) {
            candidate.setCreateBy(StpUtil.getLoginIdAsLong());
        }

        candidateMapper.insert(candidate);

        // 更新用量统计
        quotaManager.incrementUsage("candidate", 1);

        log.info("创建候选人成功: {}", candidate.getId());
        return candidate.getId();
    }

    @Override
    @Transactional
    public void updateCandidate(CandidateDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("候选人ID不能为空");
        }

        Candidate existing = candidateMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.CANDIDATE_NOT_EXIST);
        }

        Candidate candidate = new Candidate();
        BeanUtil.copyProperties(dto, candidate);

        // 处理技能和标签
        if (dto.getSkills() != null) {
            candidate.setSkills(JSONUtil.toJsonStr(dto.getSkills()));
        }
        if (dto.getTags() != null) {
            candidate.setTags(JSONUtil.toJsonStr(dto.getTags()));
        }

        if (StpUtil.isLogin()) {
            candidate.setUpdateBy(StpUtil.getLoginIdAsLong());
        }

        candidateMapper.updateById(candidate);
        log.info("更新候选人成功: {}", dto.getId());
    }

    @Override
    @Transactional
    public void deleteCandidate(Long id) {
        Candidate candidate = candidateMapper.selectById(id);
        if (candidate == null) {
            throw new BusinessException(ResultCode.CANDIDATE_NOT_EXIST);
        }

        candidateMapper.deleteById(id);

        // 减少用量统计
        quotaManager.incrementUsage("candidate", -1);

        log.info("删除候选人成功: {}", id);
    }

    @Override
    public Candidate findByPhoneOrEmail(String phone, String email) {
        return candidateManager.findByPhoneOrEmail(phone, email);
    }

    @Override
    @Transactional
    public Candidate createFromResume(Long resumeId) {
        // 委托给 Manager 处理复杂逻辑
        return candidateManager.createOrUpdateFromResume(resumeId);
    }
}
