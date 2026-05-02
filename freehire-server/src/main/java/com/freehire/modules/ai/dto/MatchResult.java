package com.freehire.modules.ai.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 简历-职位匹配结果
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
public class MatchResult {

    /**
     * 总体匹配分数（0-100）
     */
    private Integer score;

    /**
     * 匹配等级（A/B/C/D）
     */
    private String level;

    /**
     * 匹配概述
     */
    private String summary;

    /**
     * 各维度得分
     */
    private DimensionScores dimensions;

    /**
     * 匹配亮点
     */
    private List<String> highlights;

    /**
     * 不足/风险点
     */
    private List<String> concerns;

    /**
     * 面试建议
     */
    private List<String> interviewSuggestions;

    /**
     * 推荐程度说明
     */
    private String recommendation;

    /**
     * 匹配的技能列表
     */
    private List<String> matchedSkills;

    /**
     * 缺失的技能列表
     */
    private List<String> missingSkills;

    /**
     * 优势列表
     */
    private List<String> strengths;

    /**
     * 不足列表
     */
    private List<String> weaknesses;

    // ========== 快捷方法（兼容不同命名风格） ==========

    /**
     * 获取总体匹配分数（别名）
     */
    public Integer getOverallScore() {
        return score;
    }

    /**
     * 设置总体匹配分数
     */
    public void setOverallScore(Integer overallScore) {
        this.score = overallScore;
    }

    /**
     * 获取匹配分数（别名）
     */
    public Integer getMatchScore() {
        return score;
    }

    /**
     * 获取技能匹配分数
     */
    public Integer getSkillScore() {
        return dimensions != null ? dimensions.getSkillMatch() : null;
    }

    /**
     * 设置技能匹配分数
     */
    public void setSkillScore(Integer skillScore) {
        ensureDimensions().setSkillMatch(skillScore);
    }

    /**
     * 获取经验匹配分数
     */
    public Integer getExperienceScore() {
        return dimensions != null ? dimensions.getExperienceMatch() : null;
    }

    /**
     * 设置经验匹配分数
     */
    public void setExperienceScore(Integer experienceScore) {
        ensureDimensions().setExperienceMatch(experienceScore);
    }

    /**
     * 获取学历匹配分数
     */
    public Integer getEducationScore() {
        return dimensions != null ? dimensions.getEducationMatch() : null;
    }

    /**
     * 设置学历匹配分数
     */
    public void setEducationScore(Integer educationScore) {
        ensureDimensions().setEducationMatch(educationScore);
    }

    private DimensionScores ensureDimensions() {
        if (dimensions == null) {
            dimensions = new DimensionScores();
        }
        return dimensions;
    }

    @Data
    public static class DimensionScores {
        /**
         * 技能匹配度
         */
        private Integer skillMatch;

        /**
         * 经验匹配度
         */
        private Integer experienceMatch;

        /**
         * 学历匹配度
         */
        private Integer educationMatch;

        /**
         * 行业匹配度
         */
        private Integer industryMatch;

        /**
         * 薪资匹配度
         */
        private Integer salaryMatch;
    }
}

