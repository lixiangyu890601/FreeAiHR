package com.freehire.modules.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 面试DTO
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@Schema(description = "面试DTO")
public class InterviewDTO {

    @Schema(description = "面试ID（编辑时需要）")
    private Long id;

    @Schema(description = "申请ID")
    @NotNull(message = "申请ID不能为空")
    private Long applicationId;

    @Schema(description = "面试轮次")
    private Integer round;

    @Schema(description = "面试类型")
    private String interviewType;

    @Schema(description = "面试时间")
    @NotNull(message = "面试时间不能为空")
    private LocalDateTime interviewTime;

    @Schema(description = "面试时长（分钟）")
    private Integer duration;

    @Schema(description = "面试地点")
    private String location;

    @Schema(description = "会议链接")
    private String meetingLink;

    @Schema(description = "面试官ID列表")
    private List<Long> interviewerIds;

    @Schema(description = "备注")
    private String remark;
}

