package com.freehire.modules.candidate.dto;

import com.freehire.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 候选人查询参数
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "候选人查询参数")
public class CandidateQuery extends BasePageQuery {

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "学历")
    private String education;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "标签（逗号分隔）")
    private String tags;
}

