package com.freehire.modules.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freehire.common.response.R;
import com.freehire.modules.candidate.entity.Application;
import com.freehire.modules.candidate.mapper.ApplicationMapper;
import com.freehire.modules.candidate.mapper.CandidateMapper;
import com.freehire.modules.interview.mapper.InterviewMapper;
import com.freehire.modules.job.mapper.JobMapper;
import com.freehire.modules.resume.mapper.ResumeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 数据报表控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "数据报表")
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final JobMapper jobMapper;
    private final ResumeMapper resumeMapper;
    private final CandidateMapper candidateMapper;
    private final ApplicationMapper applicationMapper;
    private final InterviewMapper interviewMapper;

    @Operation(summary = "获取概览数据")
    @GetMapping("/overview")
    public R<Map<String, Object>> getOverview() {
        Map<String, Object> data = new HashMap<>();
        
        // 职位统计
        data.put("totalJobs", jobMapper.countJobs());
        data.put("openJobs", jobMapper.countOpenJobs());
        
        // 简历统计
        data.put("totalResumes", resumeMapper.countResumes());
        
        // 候选人统计
        data.put("totalCandidates", candidateMapper.countCandidates());
        
        // 今日面试
        data.put("todayInterviews", interviewMapper.countTodayInterviews());
        
        // 各阶段人数
        data.put("stageDistribution", applicationMapper.countByStage());
        
        return R.ok(data);
    }

    @Operation(summary = "获取招聘趋势")
    @GetMapping("/trend")
    public R<Map<String, Object>> getTrend(
            @RequestParam(defaultValue = "7") Integer days) {
        
        Map<String, Object> result = new HashMap<>();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        List<String> dates = new ArrayList<>();
        List<Long> resumeCounts = new ArrayList<>();
        List<Long> interviewCounts = new ArrayList<>();
        List<Long> offerCounts = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dates.add(date.format(formatter));
            
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
            
            // 简历数（按简历上传时间统计）
            long resumeCount = resumeMapper.countByTimeRange(dayStart, dayEnd);
            resumeCounts.add(resumeCount);
            
            // 面试数（按阶段变更时间统计）
            Long interviewCount = applicationMapper.selectCount(new LambdaQueryWrapper<Application>()
                    .in(Application::getStage, Arrays.asList("interviewing", "interview_passed"))
                    .ge(Application::getStageUpdateTime, dayStart)
                    .lt(Application::getStageUpdateTime, dayEnd));
            interviewCounts.add(interviewCount);
            
            // Offer数
            Long offerCount = applicationMapper.selectCount(new LambdaQueryWrapper<Application>()
                    .in(Application::getStage, Arrays.asList("offered", "onboarded"))
                    .ge(Application::getStageUpdateTime, dayStart)
                    .lt(Application::getStageUpdateTime, dayEnd));
            offerCounts.add(offerCount);
        }
        
        result.put("dates", dates);
        result.put("resumeCounts", resumeCounts);
        result.put("interviewCounts", interviewCounts);
        result.put("offerCounts", offerCounts);
        
        return R.ok(result);
    }

    @Operation(summary = "获取招聘漏斗")
    @GetMapping("/funnel")
    public R<List<Map<String, Object>>> getFunnel(@RequestParam(required = false) Long jobId) {
        List<Map<String, Object>> funnel = new ArrayList<>();
        
        List<Map<String, Object>> stageCounts;
        if (jobId != null) {
            stageCounts = applicationMapper.countByStageForJob(jobId);
        } else {
            stageCounts = applicationMapper.countByStage();
        }
        
        Map<String, Long> countMap = new HashMap<>();
        for (Map<String, Object> item : stageCounts) {
            countMap.put((String) item.get("stage"), ((Number) item.get("count")).longValue());
        }
        
        // 定义漏斗阶段
        String[][] stages = {
                {"new", "新投递"},
                {"filtered", "初筛通过"},
                {"interview_pending", "待面试"},
                {"interviewing", "面试中"},
                {"interview_passed", "面试通过"},
                {"offered", "已发Offer"},
                {"onboarded", "已入职"}
        };
        
        for (String[] stage : stages) {
            Map<String, Object> item = new HashMap<>();
            item.put("stage", stage[0]);
            item.put("name", stage[1]);
            item.put("count", countMap.getOrDefault(stage[0], 0L));
            funnel.add(item);
        }
        
        return R.ok(funnel);
    }

    @Operation(summary = "获取来源分析")
    @GetMapping("/source-analysis")
    public R<List<Map<String, Object>>> getSourceAnalysis() {
        // 从简历表统计来源
        List<Map<String, Object>> result = resumeMapper.countBySource();
        return R.ok(result);
    }

    @Operation(summary = "获取职位效果分析")
    @GetMapping("/job-analysis")
    public R<List<Map<String, Object>>> getJobAnalysis() {
        // 获取各职位的申请统计
        List<Map<String, Object>> result = new ArrayList<>();
        
        List<Map<String, Object>> stageCounts = applicationMapper.countByStage();
        // 简化实现：返回整体数据
        Map<String, Object> overall = new HashMap<>();
        overall.put("jobTitle", "全部职位");
        
        long total = 0;
        long interviewed = 0;
        long offered = 0;
        
        for (Map<String, Object> item : stageCounts) {
            String stage = (String) item.get("stage");
            long count = ((Number) item.get("count")).longValue();
            total += count;
            if (Arrays.asList("interviewing", "interview_passed", "offer_pending", "offered", "onboarded").contains(stage)) {
                interviewed += count;
            }
            if (Arrays.asList("offered", "onboarded").contains(stage)) {
                offered += count;
            }
        }
        
        overall.put("applyCount", total);
        overall.put("interviewCount", interviewed);
        overall.put("offerCount", offered);
        overall.put("interviewRate", total > 0 ? String.format("%.1f%%", interviewed * 100.0 / total) : "0%");
        overall.put("offerRate", interviewed > 0 ? String.format("%.1f%%", offered * 100.0 / interviewed) : "0%");
        
        result.add(overall);
        
        return R.ok(result);
    }
}

