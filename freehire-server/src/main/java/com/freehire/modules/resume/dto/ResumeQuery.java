package com.freehire.modules.resume.dto;

import com.freehire.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 简历查询参数
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "简历查询参数")
public class ResumeQuery extends BasePageQuery {

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "解析状态")
    private String parseStatus;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "学历")
    private String education;
}

