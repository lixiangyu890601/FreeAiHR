package com.freehire.modules.interview.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.modules.interview.dto.InterviewDTO;
import com.freehire.modules.interview.dto.InterviewFeedbackDTO;
import com.freehire.modules.interview.dto.InterviewQuery;
import com.freehire.modules.interview.entity.Interview;

import java.time.LocalDate;
import java.util.List;

/**
 * 面试服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface InterviewService {

    /**
     * 分页查询面试
     */
    IPage<Interview> getInterviewPage(InterviewQuery query);

    /**
     * 获取面试详情
     */
    Interview getInterviewById(Long id);

    /**
     * 创建面试
     */
    Long createInterview(InterviewDTO dto);

    /**
     * 更新面试
     */
    void updateInterview(InterviewDTO dto);

    /**
     * 取消面试
     */
    void cancelInterview(Long id, String reason);

    /**
     * 提交面试反馈
     */
    void submitFeedback(InterviewFeedbackDTO dto);

    /**
     * 获取申请的所有面试
     */
    List<Interview> getInterviewsByApplicationId(Long applicationId);

    /**
     * 获取某日期范围的面试
     */
    List<Interview> getInterviewsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 获取面试官的面试安排
     */
    List<Interview> getInterviewsByInterviewerId(Long interviewerId);

    /**
     * 统计今日面试数量
     */
    int countTodayInterviews();

    /**
     * AI生成面试问题
     */
    List<String> generateInterviewQuestions(Long interviewId);
}

