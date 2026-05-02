package com.freehire.modules.interview.dto;

import com.freehire.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 面试查询参数
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "面试查询参数")
public class InterviewQuery extends BasePageQuery {

    @Schema(description = "申请ID")
    private Long applicationId;

    @Schema(description = "候选人ID")
    private Long candidateId;

    @Schema(description = "职位ID")
    private Long jobId;

    @Schema(description = "面试类型")
    private String interviewType;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "结果")
    private String result;

    @Schema(description = "面试日期（开始）")
    private LocalDate startDate;

    @Schema(description = "面试日期（结束）")
    private LocalDate endDate;

    @Schema(description = "面试官ID")
    private Long interviewerId;
}

