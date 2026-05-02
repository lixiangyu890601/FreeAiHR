package com.freehire.modules.interview.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freehire.common.exception.BusinessException;
import com.freehire.modules.candidate.entity.Application;
import com.freehire.modules.candidate.mapper.ApplicationMapper;
import com.freehire.modules.candidate.service.ApplicationService;
import com.freehire.modules.candidate.dto.StageChangeDTO;
import com.freehire.modules.interview.dto.InterviewDTO;
import com.freehire.modules.interview.dto.InterviewFeedbackDTO;
import com.freehire.modules.interview.dto.InterviewQuery;
import com.freehire.modules.interview.entity.Interview;
import com.freehire.modules.interview.manager.InterviewManager;
import com.freehire.modules.interview.mapper.InterviewMapper;
import com.freehire.modules.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 面试服务实现
 * 简化版：复杂业务逻辑委托给 InterviewManager
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewMapper interviewMapper;
    private final ApplicationMapper applicationMapper;
    private final ApplicationService applicationService;
    private final InterviewManager interviewManager;

    @Override
    public IPage<Interview> getInterviewPage(InterviewQuery query) {
        Page<Interview> page = new Page<>(query.getCurrent(), query.getSize());

        LambdaQueryWrapper<Interview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getApplicationId() != null, Interview::getApplicationId, query.getApplicationId())
               .eq(query.getCandidateId() != null, Interview::getCandidateId, query.getCandidateId())
               .eq(query.getJobId() != null, Interview::getJobId, query.getJobId())
               .eq(StringUtils.hasText(query.getInterviewType()), Interview::getInterviewType, query.getInterviewType())
               .eq(StringUtils.hasText(query.getStatus()), Interview::getStatus, query.getStatus())
               .eq(StringUtils.hasText(query.getResult()), Interview::getResult, query.getResult())
               .ge(query.getStartDate() != null, Interview::getInterviewTime, 
                       query.getStartDate() != null ? query.getStartDate().atStartOfDay() : null)
               .le(query.getEndDate() != null, Interview::getInterviewTime,
                       query.getEndDate() != null ? query.getEndDate().atTime(LocalTime.MAX) : null)
               .like(query.getInterviewerId() != null, Interview::getInterviewerIds, query.getInterviewerId())
               .orderByDesc(Interview::getInterviewTime);

        IPage<Interview> result = interviewMapper.selectPage(page, wrapper);

        // 填充关联信息
        interviewManager.fillInterviewInfoBatch(result.getRecords());

        return result;
    }

    @Override
    public Interview getInterviewById(Long id) {
        Interview interview = interviewMapper.selectById(id);
        if (interview == null) {
            throw new BusinessException("面试不存在");
        }
        interviewManager.fillInterviewInfo(interview);
        return interview;
    }

    @Override
    @Transactional
    public Long createInterview(InterviewDTO dto) {
        // 检查申请
        Application application = applicationMapper.selectById(dto.getApplicationId());
        if (application == null) {
            throw new BusinessException("申请不存在");
        }

        Interview interview = new Interview();
        BeanUtil.copyProperties(dto, interview);
        interview.setCandidateId(application.getCandidateId());
        interview.setJobId(application.getJobId());
        interview.setStatus("scheduled");

        // 处理面试官ID
        if (dto.getInterviewerIds() != null && !dto.getInterviewerIds().isEmpty()) {
            interview.setInterviewerIds(dto.getInterviewerIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
        }

        // 默认轮次
        if (interview.getRound() == null) {
            Long count = interviewMapper.selectCount(new LambdaQueryWrapper<Interview>()
                    .eq(Interview::getApplicationId, dto.getApplicationId()));
            interview.setRound(count.intValue() + 1);
        }

        // 默认面试类型
        if (!StringUtils.hasText(interview.getInterviewType())) {
            interview.setInterviewType("onsite");
        }

        // 默认时长
        if (interview.getDuration() == null) {
            interview.setDuration(60);
        }

        if (StpUtil.isLogin()) {
            interview.setCreateBy(StpUtil.getLoginIdAsLong());
        }

        interviewMapper.insert(interview);

        // 更新申请状态
        StageChangeDTO stageChangeDTO = new StageChangeDTO();
        stageChangeDTO.setApplicationId(dto.getApplicationId());
        stageChangeDTO.setTargetStage("interview_pending");
        applicationService.changeStage(stageChangeDTO);

        log.info("创建面试成功: {}", interview.getId());
        return interview.getId();
    }

    @Override
    @Transactional
    public void updateInterview(InterviewDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("面试ID不能为空");
        }

        Interview existing = interviewMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("面试不存在");
        }

        Interview interview = new Interview();
        interview.setId(dto.getId());
        interview.setRound(dto.getRound());
        interview.setInterviewType(dto.getInterviewType());
        interview.setInterviewTime(dto.getInterviewTime());
        interview.setDuration(dto.getDuration());
        interview.setLocation(dto.getLocation());
        interview.setMeetingLink(dto.getMeetingLink());
        interview.setRemark(dto.getRemark());

        if (dto.getInterviewerIds() != null) {
            interview.setInterviewerIds(dto.getInterviewerIds().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
        }

        if (StpUtil.isLogin()) {
            interview.setUpdateBy(StpUtil.getLoginIdAsLong());
        }

        interviewMapper.updateById(interview);
        log.info("更新面试成功: {}", dto.getId());
    }

    @Override
    @Transactional
    public void cancelInterview(Long id, String reason) {
        Interview interview = interviewMapper.selectById(id);
        if (interview == null) {
            throw new BusinessException("面试不存在");
        }

        interview.setStatus("cancelled");
        interview.setRemark(reason);

        if (StpUtil.isLogin()) {
            interview.setUpdateBy(StpUtil.getLoginIdAsLong());
        }

        interviewMapper.updateById(interview);
        log.info("取消面试成功: {}", id);
    }

    @Override
    @Transactional
    public void submitFeedback(InterviewFeedbackDTO dto) {
        Interview interview = interviewMapper.selectById(dto.getInterviewId());
        if (interview == null) {
            throw new BusinessException("面试不存在");
        }

        interview.setStatus("completed");
        interview.setScore(dto.getScore());
        interview.setResult(dto.getResult());
        interview.setFeedback(dto.getFeedback());
        if (StringUtils.hasText(dto.getRemark())) {
            interview.setRemark(dto.getRemark());
        }

        if (StpUtil.isLogin()) {
            interview.setUpdateBy(StpUtil.getLoginIdAsLong());
        }

        interviewMapper.updateById(interview);

        // 根据面试结果更新申请状态
        if ("pass".equals(dto.getResult())) {
            StageChangeDTO stageChangeDTO = new StageChangeDTO();
            stageChangeDTO.setApplicationId(interview.getApplicationId());
            stageChangeDTO.setTargetStage("interview_passed");
            applicationService.changeStage(stageChangeDTO);
        } else if ("fail".equals(dto.getResult())) {
            StageChangeDTO stageChangeDTO = new StageChangeDTO();
            stageChangeDTO.setApplicationId(interview.getApplicationId());
            stageChangeDTO.setTargetStage("rejected");
            stageChangeDTO.setRejectReason("面试未通过");
            applicationService.changeStage(stageChangeDTO);
        }

        log.info("提交面试反馈成功: interviewId={}, result={}", dto.getInterviewId(), dto.getResult());
    }

    @Override
    public List<Interview> getInterviewsByApplicationId(Long applicationId) {
        List<Interview> interviews = interviewMapper.selectList(new LambdaQueryWrapper<Interview>()
                .eq(Interview::getApplicationId, applicationId)
                .orderByAsc(Interview::getRound));
        interviewManager.fillInterviewInfoBatch(interviews);
        return interviews;
    }

    @Override
    public List<Interview> getInterviewsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        List<Interview> interviews = interviewMapper.selectByTimeRange(startTime, endTime);
        interviewManager.fillInterviewInfoBatch(interviews);
        return interviews;
    }

    @Override
    public List<Interview> getInterviewsByInterviewerId(Long interviewerId) {
        List<Interview> interviews = interviewMapper.selectByInterviewerId(interviewerId);
        interviewManager.fillInterviewInfoBatch(interviews);
        return interviews;
    }

    @Override
    public int countTodayInterviews() {
        return interviewMapper.countTodayInterviews();
    }

    @Override
    public List<String> generateInterviewQuestions(Long interviewId) {
        // 委托给 Manager 处理
        return interviewManager.generateInterviewQuestions(interviewId);
    }
}
