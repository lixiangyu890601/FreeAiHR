package com.freehire.modules.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 求职申请VO（包含关联信息）
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@Schema(description = "求职申请VO")
public class ApplicationVO {

    @Schema(description = "申请ID")
    private Long id;

    @Schema(description = "候选人ID")
    private Long candidateId;

    @Schema(description = "职位ID")
    private Long jobId;

    @Schema(description = "简历ID")
    private Long resumeId;

    @Schema(description = "当前阶段")
    private String stage;

    @Schema(description = "AI匹配分数")
    private Integer matchScore;

    @Schema(description = "匹配分析")
    private String matchAnalysis;

    @Schema(description = "负责HR ID")
    private Long hrUserId;

    @Schema(description = "淘汰原因")
    private String rejectReason;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "申请时间")
    private LocalDateTime applyTime;

    @Schema(description = "阶段更新时间")
    private LocalDateTime stageUpdateTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // ========== 关联信息 ==========

    @Schema(description = "候选人姓名")
    private String candidateName;

    @Schema(description = "候选人手机")
    private String candidatePhone;

    @Schema(description = "候选人邮箱")
    private String candidateEmail;

    @Schema(description = "候选人学历")
    private String candidateEducation;

    @Schema(description = "候选人工作年限")
    private Integer candidateWorkYears;

    @Schema(description = "候选人当前公司")
    private String candidateCurrentCompany;

    @Schema(description = "候选人当前职位")
    private String candidateCurrentPosition;

    @Schema(description = "职位名称")
    private String jobTitle;

    @Schema(description = "职位部门")
    private String jobDeptName;

    @Schema(description = "职位城市")
    private String jobCity;

    @Schema(description = "HR姓名")
    private String hrName;
}

