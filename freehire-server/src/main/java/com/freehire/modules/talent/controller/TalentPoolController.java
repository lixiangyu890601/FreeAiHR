package com.freehire.modules.talent.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freehire.common.response.PageResult;
import com.freehire.common.response.R;
import com.freehire.modules.candidate.entity.Candidate;
import com.freehire.modules.candidate.mapper.CandidateMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 人才库控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "人才库")
@RestController
@RequestMapping("/talent")
@RequiredArgsConstructor
public class TalentPoolController {

    private final CandidateMapper candidateMapper;

    @Operation(summary = "人才搜索")
    @GetMapping("/search")
    public R<PageResult<Candidate>> searchTalent(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String education,
            @RequestParam(required = false) Integer minWorkYears,
            @RequestParam(required = false) Integer maxWorkYears,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String tags) {

        Page<Candidate> page = new Page<>(current, size);

        LambdaQueryWrapper<Candidate> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索（姓名、职位、公司）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Candidate::getName, keyword)
                    .or().like(Candidate::getCurrentPosition, keyword)
                    .or().like(Candidate::getCurrentCompany, keyword)
                    .or().like(Candidate::getExpectPosition, keyword)
            );
        }
        
        // 学历筛选
        wrapper.eq(StringUtils.hasText(education), Candidate::getEducation, education);
        
        // 工作年限筛选
        wrapper.ge(minWorkYears != null, Candidate::getWorkYears, minWorkYears);
        wrapper.le(maxWorkYears != null, Candidate::getWorkYears, maxWorkYears);
        
        // 城市筛选
        wrapper.like(StringUtils.hasText(city), Candidate::getCity, city);
        
        // 技能筛选
        if (StringUtils.hasText(skills)) {
            for (String skill : skills.split(",")) {
                wrapper.like(Candidate::getSkills, skill.trim());
            }
        }
        
        // 标签筛选
        if (StringUtils.hasText(tags)) {
            for (String tag : tags.split(",")) {
                wrapper.like(Candidate::getTags, tag.trim());
            }
        }
        
        wrapper.orderByDesc(Candidate::getCreateTime);

        IPage<Candidate> result = candidateMapper.selectPage(page, wrapper);
        return R.ok(PageResult.of(result));
    }

    @Operation(summary = "添加/更新标签")
    @PostMapping("/{id}/tags")
    public R<Void> updateTags(@PathVariable Long id, @RequestBody Map<String, String> params) {
        Candidate candidate = candidateMapper.selectById(id);
        if (candidate == null) {
            return R.fail("候选人不存在");
        }
        candidate.setTags(params.get("tags"));
        candidateMapper.updateById(candidate);
        return R.ok();
    }

    @Operation(summary = "获取常用标签")
    @GetMapping("/tags")
    public R<List<String>> getPopularTags() {
        return R.ok(List.of(
                "优秀", "潜力股", "待跟进", "高意向",
                "技术专家", "管理经验", "大厂背景", "海归",
                "稀缺人才", "已面试", "已发Offer", "黑名单"
        ));
    }

    @Operation(summary = "获取学历选项")
    @GetMapping("/educations")
    public R<List<String>> getEducationOptions() {
        return R.ok(List.of("博士", "硕士", "本科", "大专", "高中及以下"));
    }

    @Operation(summary = "获取城市选项")
    @GetMapping("/cities")
    public R<List<String>> getCityOptions() {
        return R.ok(List.of(
                "北京", "上海", "广州", "深圳", "杭州", "成都",
                "南京", "武汉", "西安", "苏州", "天津", "重庆"
        ));
    }
}

