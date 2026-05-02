package com.freehire.modules.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 阶段变更DTO
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@Schema(description = "阶段变更DTO")
public class StageChangeDTO {

    @Schema(description = "申请ID")
    @NotNull(message = "申请ID不能为空")
    private Long applicationId;

    @Schema(description = "目标阶段")
    @NotBlank(message = "目标阶段不能为空")
    private String targetStage;

    @Schema(description = "淘汰原因（rejected时必填）")
    private String rejectReason;

    @Schema(description = "备注")
    private String remark;
}

