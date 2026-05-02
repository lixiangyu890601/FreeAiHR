package com.freehire.modules.candidate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freehire.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 求职申请/投递记录实体
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("application")
public class Application extends BaseEntity {

    /**
     * 候选人ID
     */
    private Long candidateId;

    /**
     * 职位ID
     */
    private Long jobId;

    /**
     * 简历ID
     */
    private Long resumeId;

    /**
     * 当前阶段
     * new-待筛选
     * filtered-初筛通过
     * interview_pending-待安排面试
     * interviewing-面试中
     * interview_passed-面试通过
     * offer_pending-待发Offer
     * offered-已发Offer
     * onboarded-已入职
     * rejected-不合适
     * withdrawn-候选人放弃
     */
    private String stage;

    /**
     * AI匹配分数（0-100）
     */
    private Integer matchScore;

    /**
     * AI匹配分析（JSON）
     */
    private String matchAnalysis;

    /**
     * 负责HR用户ID
     */
    private Long hrUserId;

    /**
     * 淘汰原因
     */
    private String rejectReason;

    /**
     * 投递来源（website-官网 upload-HR录入 email-邮件 referral-内推）
     */
    private String source;

    /**
     * 投递时间
     */
    private LocalDateTime applyTime;

    /**
     * 最后更新阶段时间
     */
    private LocalDateTime stageUpdateTime;

    /**
     * 备注
     */
    private String remark;

    // ========== 非数据库字段 ==========

    /**
     * 候选人姓名
     */
    @TableField(exist = false)
    private String candidateName;

    /**
     * 候选人手机
     */
    @TableField(exist = false)
    private String candidatePhone;

    /**
     * 候选人邮箱
     */
    @TableField(exist = false)
    private String candidateEmail;

    /**
     * 职位名称
     */
    @TableField(exist = false)
    private String jobTitle;

    /**
     * HR姓名
     */
    @TableField(exist = false)
    private String hrName;
}

