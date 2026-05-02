package com.freehire.modules.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 求职申请DTO
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@Schema(description = "求职申请DTO")
public class ApplicationDTO {

    @Schema(description = "申请ID（编辑时需要）")
    private Long id;

    @Schema(description = "候选人ID")
    @NotNull(message = "候选人ID不能为空")
    private Long candidateId;

    @Schema(description = "职位ID")
    @NotNull(message = "职位ID不能为空")
    private Long jobId;

    @Schema(description = "简历ID")
    private Long resumeId;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "备注")
    private String remark;
}

