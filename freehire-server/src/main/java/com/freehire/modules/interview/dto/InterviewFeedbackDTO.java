package com.freehire.modules.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 面试反馈DTO
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@Schema(description = "面试反馈DTO")
public class InterviewFeedbackDTO {

    @Schema(description = "面试ID")
    @NotNull(message = "面试ID不能为空")
    private Long interviewId;

    @Schema(description = "评分（1-100）")
    private Integer score;

    @Schema(description = "结果（pass/fail/pending）")
    @NotBlank(message = "面试结果不能为空")
    private String result;

    @Schema(description = "反馈内容")
    private String feedback;

    @Schema(description = "备注")
    private String remark;
}

