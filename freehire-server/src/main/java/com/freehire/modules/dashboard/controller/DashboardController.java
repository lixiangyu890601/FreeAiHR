package com.freehire.modules.dashboard.controller;

import com.freehire.common.response.R;
import com.freehire.modules.candidate.mapper.CandidateMapper;
import com.freehire.modules.candidate.service.ApplicationService;
import com.freehire.modules.interview.service.InterviewService;
import com.freehire.modules.job.mapper.JobMapper;
import com.freehire.modules.resume.mapper.ResumeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "仪表盘")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final JobMapper jobMapper;
    private final ResumeMapper resumeMapper;
    private final CandidateMapper candidateMapper;
    private final ApplicationService applicationService;
    private final InterviewService interviewService;

    @Operation(summary = "获取统计数据")
    @GetMapping("/stats")
    public R<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 职位统计
        stats.put("jobCount", jobMapper.countJobs());
        stats.put("openJobCount", jobMapper.countOpenJobs());
        
        // 简历统计
        stats.put("resumeCount", resumeMapper.countResumes());
        
        // 候选人统计
        stats.put("candidateCount", candidateMapper.countCandidates());
        
        // 各阶段统计
        Map<String, Long> stageCount = applicationService.countByStage();
        stats.put("stageCount", stageCount);
        
        // 今日面试
        stats.put("todayInterviewCount", interviewService.countTodayInterviews());
        
        return R.ok(stats);
    }

    @Operation(summary = "获取招聘漏斗数据")
    @GetMapping("/funnel")
    public R<List<Map<String, Object>>> getFunnel() {
        return R.ok(applicationService.getFunnelData(null));
    }
}

