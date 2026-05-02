package com.freehire.modules.candidate.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.common.response.PageResult;
import com.freehire.common.response.R;
import com.freehire.modules.candidate.dto.CandidateDTO;
import com.freehire.modules.candidate.dto.CandidateQuery;
import com.freehire.modules.candidate.entity.Candidate;
import com.freehire.modules.candidate.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 候选人管理控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "候选人管理")
@RestController
@RequestMapping("/candidate")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @Operation(summary = "分页查询候选人")
    @GetMapping("/page")
    public R<PageResult<Candidate>> getCandidatePage(CandidateQuery query) {
        IPage<Candidate> page = candidateService.getCandidatePage(query);
        return R.ok(PageResult.of(page));
    }

    @Operation(summary = "获取候选人详情")
    @GetMapping("/{id}")
    public R<Candidate> getCandidateById(@PathVariable Long id) {
        return R.ok(candidateService.getCandidateById(id));
    }

    @Operation(summary = "新增候选人")
    @PostMapping
    public R<Long> createCandidate(@Valid @RequestBody CandidateDTO dto) {
        return R.ok(candidateService.createCandidate(dto));
    }

    @Operation(summary = "更新候选人")
    @PutMapping
    public R<Void> updateCandidate(@Valid @RequestBody CandidateDTO dto) {
        candidateService.updateCandidate(dto);
        return R.ok();
    }

    @Operation(summary = "删除候选人")
    @DeleteMapping("/{id}")
    public R<Void> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return R.ok();
    }

    @Operation(summary = "从简历创建候选人")
    @PostMapping("/from-resume/{resumeId}")
    public R<Candidate> createFromResume(@PathVariable Long resumeId) {
        return R.ok(candidateService.createFromResume(resumeId));
    }
}

