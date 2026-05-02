package com.freehire.modules.candidate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.modules.candidate.dto.CandidateDTO;
import com.freehire.modules.candidate.dto.CandidateQuery;
import com.freehire.modules.candidate.entity.Candidate;

/**
 * 候选人服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface CandidateService {

    /**
     * 分页查询候选人
     */
    IPage<Candidate> getCandidatePage(CandidateQuery query);

    /**
     * 获取候选人详情
     */
    Candidate getCandidateById(Long id);

    /**
     * 新增候选人
     */
    Long createCandidate(CandidateDTO dto);

    /**
     * 更新候选人
     */
    void updateCandidate(CandidateDTO dto);

    /**
     * 删除候选人
     */
    void deleteCandidate(Long id);

    /**
     * 根据手机号或邮箱查找候选人
     */
    Candidate findByPhoneOrEmail(String phone, String email);

    /**
     * 从简历创建候选人
     */
    Candidate createFromResume(Long resumeId);
}

