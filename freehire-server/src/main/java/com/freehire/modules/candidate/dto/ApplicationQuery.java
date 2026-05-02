package com.freehire.modules.candidate.dto;

import com.freehire.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 求职申请查询参数
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "求职申请查询参数")
public class ApplicationQuery extends BasePageQuery {

    @Schema(description = "候选人ID")
    private Long candidateId;

    @Schema(description = "职位ID")
    private Long jobId;

    @Schema(description = "当前阶段")
    private String stage;

    @Schema(description = "候选人姓名")
    private String candidateName;

    @Schema(description = "职位名称")
    private String jobTitle;

    @Schema(description = "来源")
    private String source;
}

