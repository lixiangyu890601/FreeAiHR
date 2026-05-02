package com.freehire.modules.subscription.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订阅信息DTO（包含套餐信息和当前使用量）
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
public class SubscriptionInfo {

    // ========== 套餐基本信息 ==========

    private String planCode;
    private String planName;
    private String description;
    private BigDecimal price;
    private String billingCycle;

    // ========== 功能权限 ==========

    private Boolean featureAiParse;
    private Boolean featureAiMatch;
    private Boolean featureAiGenerateJd;
    private Boolean featureTalentPool;
    private Boolean featureDataReport;
    private Boolean featureCareerSite;
    private Boolean featureApiAccess;

    // ========== 用量限制 ==========

    private Integer limitJobCount;
    private Integer limitResumeCount;
    private Integer limitUserCount;
    private Integer limitAiParseMonthly;
    private Integer limitAiMatchMonthly;

    // ========== 当前用量 ==========

    private Integer currentJobCount;
    private Integer currentResumeCount;
    private Integer currentUserCount;
    private Integer currentAiParseCount;
    private Integer currentAiMatchCount;

    // ========== 订阅状态 ==========

    private Integer subscriptionStatus;
    private LocalDateTime expiresAt;

    /**
     * 检查功能是否可用
     */
    public boolean hasFeature(String featureCode) {
        return switch (featureCode) {
            case "ai_parse" -> Boolean.TRUE.equals(featureAiParse);
            case "ai_match" -> Boolean.TRUE.equals(featureAiMatch);
            case "ai_generate_jd" -> Boolean.TRUE.equals(featureAiGenerateJd);
            case "talent_pool" -> Boolean.TRUE.equals(featureTalentPool);
            case "data_report" -> Boolean.TRUE.equals(featureDataReport);
            case "career_site" -> Boolean.TRUE.equals(featureCareerSite);
            case "api_access" -> Boolean.TRUE.equals(featureApiAccess);
            default -> false;
        };
    }

    /**
     * 检查用量是否超限
     * @return true表示未超限，false表示已超限
     */
    public boolean checkQuota(String quotaType) {
        return switch (quotaType) {
            case "job" -> limitJobCount == -1 || currentJobCount < limitJobCount;
            case "resume" -> limitResumeCount == -1 || currentResumeCount < limitResumeCount;
            case "user" -> limitUserCount == -1 || currentUserCount < limitUserCount;
            case "ai_parse" -> limitAiParseMonthly == -1 || currentAiParseCount < limitAiParseMonthly;
            case "ai_match" -> limitAiMatchMonthly == -1 || currentAiMatchCount < limitAiMatchMonthly;
            default -> true;
        };
    }
}

