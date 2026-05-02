package com.freehire.modules.candidate.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.common.annotation.CheckQuota;
import com.freehire.common.annotation.RequireFeature;
import com.freehire.common.response.PageResult;
import com.freehire.common.response.R;
import com.freehire.modules.candidate.dto.*;
import com.freehire.modules.candidate.entity.Application;
import com.freehire.modules.candidate.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 求职申请控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "求职申请管理")
@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "分页查询申请")
    @GetMapping("/page")
    public R<PageResult<ApplicationVO>> getApplicationPage(ApplicationQuery query) {
        IPage<ApplicationVO> page = applicationService.getApplicationPage(query);
        return R.ok(PageResult.of(page));
    }

    @Operation(summary = "获取申请详情")
    @GetMapping("/{id}")
    public R<Application> getApplicationById(@PathVariable Long id) {
        return R.ok(applicationService.getApplicationById(id));
    }

    @Operation(summary = "创建申请")
    @PostMapping
    public R<Long> createApplication(@Valid @RequestBody ApplicationDTO dto) {
        return R.ok(applicationService.createApplication(dto));
    }

    @Operation(summary = "更新申请")
    @PutMapping
    public R<Void> updateApplication(@Valid @RequestBody ApplicationDTO dto) {
        applicationService.updateApplication(dto);
        return R.ok();
    }

    @Operation(summary = "删除申请")
    @DeleteMapping("/{id}")
    public R<Void> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return R.ok();
    }

    @Operation(summary = "变更阶段")
    @PostMapping("/change-stage")
    public R<Void> changeStage(@Valid @RequestBody StageChangeDTO dto) {
        applicationService.changeStage(dto);
        return R.ok();
    }

    @Operation(summary = "批量变更阶段")
    @PostMapping("/batch-change-stage")
    public R<Void> batchChangeStage(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        String targetStage = (String) params.get("targetStage");
        String reason = (String) params.get("reason");
        applicationService.batchChangeStage(ids, targetStage, reason);
        return R.ok();
    }

    @Operation(summary = "AI智能匹配")
    @PostMapping("/{id}/ai-match")
    @RequireFeature(value = "ai_match", name = "AI智能匹配")
    @CheckQuota("ai_match")
    public R<Void> aiMatch(@PathVariable Long id) {
        applicationService.aiMatch(id);
        return R.ok();
    }

    @Operation(summary = "获取候选人的所有申请")
    @GetMapping("/by-candidate/{candidateId}")
    public R<List<Application>> getApplicationsByCandidateId(@PathVariable Long candidateId) {
        return R.ok(applicationService.getApplicationsByCandidateId(candidateId));
    }

    @Operation(summary = "获取职位的所有申请")
    @GetMapping("/by-job/{jobId}")
    public R<List<Application>> getApplicationsByJobId(@PathVariable Long jobId) {
        return R.ok(applicationService.getApplicationsByJobId(jobId));
    }

    @Operation(summary = "统计各阶段数量")
    @GetMapping("/count-by-stage")
    public R<Map<String, Long>> countByStage() {
        return R.ok(applicationService.countByStage());
    }

    @Operation(summary = "获取招聘漏斗数据")
    @GetMapping("/funnel")
    public R<List<Map<String, Object>>> getFunnelData(@RequestParam(required = false) Long jobId) {
        return R.ok(applicationService.getFunnelData(jobId));
    }

    @Operation(summary = "推荐候选人到职位")
    @PostMapping("/recommend")
    public R<Long> recommendToJob(@RequestBody Map<String, Long> params) {
        Long candidateId = params.get("candidateId");
        Long jobId = params.get("jobId");
        return R.ok(applicationService.recommendToJob(candidateId, jobId));
    }
}

