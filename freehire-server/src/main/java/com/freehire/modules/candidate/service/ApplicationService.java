package com.freehire.modules.candidate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.modules.candidate.dto.*;
import com.freehire.modules.candidate.entity.Application;

import java.util.List;
import java.util.Map;

/**
 * 求职申请服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface ApplicationService {

    /**
     * 分页查询申请
     */
    IPage<ApplicationVO> getApplicationPage(ApplicationQuery query);

    /**
     * 获取申请详情
     */
    Application getApplicationById(Long id);

    /**
     * 创建申请
     */
    Long createApplication(ApplicationDTO dto);

    /**
     * 更新申请
     */
    void updateApplication(ApplicationDTO dto);

    /**
     * 删除申请
     */
    void deleteApplication(Long id);

    /**
     * 变更阶段
     */
    void changeStage(StageChangeDTO dto);

    /**
     * 批量变更阶段
     */
    void batchChangeStage(List<Long> applicationIds, String targetStage, String reason);

    /**
     * AI匹配评分
     */
    void aiMatch(Long applicationId);

    /**
     * 获取候选人的所有申请
     */
    List<Application> getApplicationsByCandidateId(Long candidateId);

    /**
     * 获取职位的所有申请
     */
    List<Application> getApplicationsByJobId(Long jobId);

    /**
     * 统计各阶段数量
     */
    Map<String, Long> countByStage();

    /**
     * 获取招聘漏斗数据
     */
    List<Map<String, Object>> getFunnelData(Long jobId);

    /**
     * 推荐候选人到职位
     * 
     * @param candidateId 候选人ID
     * @param jobId 职位ID
     * @return 申请ID
     */
    Long recommendToJob(Long candidateId, Long jobId);
}

