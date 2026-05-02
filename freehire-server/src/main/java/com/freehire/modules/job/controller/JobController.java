package com.freehire.modules.job.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.common.annotation.CheckQuota;
import com.freehire.common.response.PageResult;
import com.freehire.common.response.R;
import com.freehire.modules.job.dto.JobDTO;
import com.freehire.modules.job.dto.JobQuery;
import com.freehire.modules.job.entity.Job;
import com.freehire.modules.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 职位管理控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "职位管理")
@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @Operation(summary = "分页查询职位")
    @GetMapping("/page")
    public R<PageResult<Job>> getJobPage(JobQuery query) {
        IPage<Job> page = jobService.getJobPage(query);
        return R.ok(PageResult.of(page));
    }

    @Operation(summary = "获取职位详情")
    @GetMapping("/{id}")
    public R<Job> getJobById(@PathVariable Long id) {
        return R.ok(jobService.getJobById(id));
    }

    @Operation(summary = "新增职位")
    @PostMapping
    @CheckQuota("job")
    public R<Long> createJob(@Valid @RequestBody JobDTO dto) {
        return R.ok(jobService.createJob(dto));
    }

    @Operation(summary = "更新职位")
    @PutMapping
    public R<Void> updateJob(@Valid @RequestBody JobDTO dto) {
        jobService.updateJob(dto);
        return R.ok();
    }

    @Operation(summary = "删除职位")
    @DeleteMapping("/{id}")
    public R<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return R.ok();
    }

    @Operation(summary = "发布职位")
    @PostMapping("/{id}/publish")
    public R<Void> publishJob(@PathVariable Long id) {
        jobService.publishJob(id);
        return R.ok();
    }

    @Operation(summary = "关闭职位")
    @PostMapping("/{id}/close")
    public R<Void> closeJob(@PathVariable Long id) {
        jobService.closeJob(id);
        return R.ok();
    }

    @Operation(summary = "暂停职位")
    @PostMapping("/{id}/pause")
    public R<Void> pauseJob(@PathVariable Long id) {
        jobService.pauseJob(id);
        return R.ok();
    }
}

