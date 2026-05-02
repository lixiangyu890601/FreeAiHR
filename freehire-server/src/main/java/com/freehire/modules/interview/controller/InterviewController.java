package com.freehire.modules.interview.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.common.response.PageResult;
import com.freehire.common.response.R;
import com.freehire.modules.interview.dto.InterviewDTO;
import com.freehire.modules.interview.dto.InterviewFeedbackDTO;
import com.freehire.modules.interview.dto.InterviewQuery;
import com.freehire.modules.interview.entity.Interview;
import com.freehire.modules.interview.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 面试管理控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "面试管理")
@RestController
@RequestMapping("/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @Operation(summary = "分页查询面试")
    @GetMapping("/page")
    public R<PageResult<Interview>> getInterviewPage(InterviewQuery query) {
        IPage<Interview> page = interviewService.getInterviewPage(query);
        return R.ok(PageResult.of(page));
    }

    @Operation(summary = "获取面试详情")
    @GetMapping("/{id}")
    public R<Interview> getInterviewById(@PathVariable Long id) {
        return R.ok(interviewService.getInterviewById(id));
    }

    @Operation(summary = "创建面试")
    @PostMapping
    public R<Long> createInterview(@Valid @RequestBody InterviewDTO dto) {
        return R.ok(interviewService.createInterview(dto));
    }

    @Operation(summary = "更新面试")
    @PutMapping
    public R<Void> updateInterview(@Valid @RequestBody InterviewDTO dto) {
        interviewService.updateInterview(dto);
        return R.ok();
    }

    @Operation(summary = "取消面试")
    @PostMapping("/{id}/cancel")
    public R<Void> cancelInterview(@PathVariable Long id, 
                                   @RequestParam(required = false) String reason) {
        interviewService.cancelInterview(id, reason);
        return R.ok();
    }

    @Operation(summary = "提交面试反馈")
    @PostMapping("/feedback")
    public R<Void> submitFeedback(@Valid @RequestBody InterviewFeedbackDTO dto) {
        interviewService.submitFeedback(dto);
        return R.ok();
    }

    @Operation(summary = "获取申请的所有面试")
    @GetMapping("/by-application/{applicationId}")
    public R<List<Interview>> getInterviewsByApplicationId(@PathVariable Long applicationId) {
        return R.ok(interviewService.getInterviewsByApplicationId(applicationId));
    }

    @Operation(summary = "获取日期范围内的面试")
    @GetMapping("/by-date-range")
    public R<List<Interview>> getInterviewsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return R.ok(interviewService.getInterviewsByDateRange(startDate, endDate));
    }

    @Operation(summary = "获取面试官的面试安排")
    @GetMapping("/by-interviewer/{interviewerId}")
    public R<List<Interview>> getInterviewsByInterviewerId(@PathVariable Long interviewerId) {
        return R.ok(interviewService.getInterviewsByInterviewerId(interviewerId));
    }

    @Operation(summary = "统计今日面试数量")
    @GetMapping("/count-today")
    public R<Integer> countTodayInterviews() {
        return R.ok(interviewService.countTodayInterviews());
    }

    @Operation(summary = "AI生成面试问题")
    @GetMapping("/{id}/generate-questions")
    public R<List<String>> generateInterviewQuestions(@PathVariable Long id) {
        return R.ok(interviewService.generateInterviewQuestions(id));
    }
}

