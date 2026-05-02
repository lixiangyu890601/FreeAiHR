package com.freehire.modules.subscription.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐配置实体
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@TableName("subscription_plan")
public class SubscriptionPlan implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 套餐编码 (free/basic/pro/enterprise)
     */
    private String planCode;

    /**
     * 套餐名称
     */
    private String planName;

    /**
     * 套餐描述
     */
    private String description;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 计费周期 (monthly/yearly/permanent)
     */
    private String billingCycle;

    // ========== 功能开关 ==========

    /**
     * AI简历解析功能
     */
    private Integer featureAiParse;

    /**
     * AI智能匹配功能
     */
    private Integer featureAiMatch;

    /**
     * AI生成JD功能
     */
    private Integer featureAiGenerateJd;

    /**
     * 人才库功能
     */
    private Integer featureTalentPool;

    /**
     * 数据报表功能
     */
    private Integer featureDataReport;

    /**
     * 招聘官网功能
     */
    private Integer featureCareerSite;

    /**
     * API对接功能
     */
    private Integer featureApiAccess;

    // ========== 用量限制 ==========

    /**
     * 职位数量限制 (-1表示无限)
     */
    private Integer limitJobCount;

    /**
     * 简历数量限制
     */
    private Integer limitResumeCount;

    /**
     * 用户数量限制
     */
    private Integer limitUserCount;

    /**
     * 每月AI解析次数限制
     */
    private Integer limitAiParseMonthly;

    /**
     * 每月AI匹配次数限制
     */
    private Integer limitAiMatchMonthly;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否默认套餐
     */
    private Integer isDefault;

    /**
     * 状态
     */
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

