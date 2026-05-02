package com.freehire.modules.resume.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.common.annotation.CheckQuota;
import com.freehire.common.annotation.RequireFeature;
import com.freehire.common.response.PageResult;
import com.freehire.common.response.R;
import com.freehire.modules.resume.dto.ResumeQuery;
import com.freehire.modules.resume.entity.Resume;
import com.freehire.modules.resume.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 简历管理控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "简历管理")
@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @Operation(summary = "分页查询简历")
    @GetMapping("/page")
    public R<PageResult<Resume>> getResumePage(ResumeQuery query) {
        IPage<Resume> page = resumeService.getResumePage(query);
        return R.ok(PageResult.of(page));
    }

    @Operation(summary = "获取简历详情")
    @GetMapping("/{id}")
    public R<Resume> getResumeById(@PathVariable Long id) {
        return R.ok(resumeService.getResumeById(id));
    }

    @Operation(summary = "上传简历")
    @PostMapping("/upload")
    @CheckQuota("resume")
    public R<Resume> uploadResume(@RequestParam("file") MultipartFile file,
                                  @RequestParam(value = "source", required = false) String source) {
        return R.ok(resumeService.uploadResume(file, source));
    }

    @Operation(summary = "删除简历")
    @DeleteMapping("/{id}")
    public R<Void> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return R.ok();
    }

    @Operation(summary = "AI解析简历")
    @PostMapping("/{id}/parse")
    @RequireFeature(value = "ai_parse", name = "AI简历解析")
    @CheckQuota("ai_parse")
    public R<Resume> parseResume(@PathVariable Long id) {
        return R.ok(resumeService.parseResume(id));
    }

    @Operation(summary = "获取简历下载链接")
    @GetMapping("/{id}/download-url")
    public R<String> getResumeDownloadUrl(@PathVariable Long id) {
        return R.ok(resumeService.getResumeDownloadUrl(id));
    }
}

