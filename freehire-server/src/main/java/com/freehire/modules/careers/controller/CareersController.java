package com.freehire.modules.careers.controller;

import com.freehire.common.response.R;
import com.freehire.modules.careers.dto.ApplyDTO;
import com.freehire.modules.careers.dto.CompanyInfoVO;
import com.freehire.modules.careers.dto.JobVO;
import com.freehire.modules.careers.service.CareersService;
import com.freehire.common.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公开招聘接口（无需登录）
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "公开招聘接口")
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class CareersController {

    private final CareersService careersService;
    private final FileService fileService;

    @Operation(summary = "获取公司信息")
    @GetMapping("/company")
    public R<CompanyInfoVO> getCompanyInfo() {
        return R.ok(careersService.getCompanyInfo());
    }

    @Operation(summary = "获取在招职位列表")
    @GetMapping("/jobs")
    public R<List<JobVO>> getJobList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String jobType) {
        return R.ok(careersService.getJobList(keyword, city, jobType));
    }

    @Operation(summary = "获取职位详情")
    @GetMapping("/jobs/{id}")
    public R<JobVO> getJobDetail(@PathVariable Long id) {
        return R.ok(careersService.getJobDetail(id));
    }

    @Operation(summary = "获取城市选项")
    @GetMapping("/cities")
    public R<List<String>> getCityOptions() {
        return R.ok(careersService.getCityOptions());
    }

    @Operation(summary = "上传简历文件")
    @PostMapping("/upload-resume")
    public R<Map<String, String>> uploadResume(@RequestParam("file") MultipartFile file) {
        String filePath = fileService.uploadFile(file, "resume");
        Map<String, String> result = new HashMap<>();
        result.put("path", filePath);
        result.put("fileName", file.getOriginalFilename());
        return R.ok(result);
    }

    @Operation(summary = "投递简历")
    @PostMapping("/apply")
    public R<Long> apply(@Valid @RequestBody ApplyDTO dto) {
        return R.ok(careersService.apply(dto));
    }

    @Operation(summary = "检查是否已投递")
    @GetMapping("/check-applied")
    public R<Boolean> checkApplied(
            @RequestParam Long jobId,
            @RequestParam String phone) {
        return R.ok(careersService.hasApplied(jobId, phone));
    }
}

