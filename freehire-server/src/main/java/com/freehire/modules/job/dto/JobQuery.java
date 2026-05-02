package com.freehire.modules.job.dto;

import com.freehire.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 职位查询参数
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "职位查询参数")
public class JobQuery extends BasePageQuery {

    @Schema(description = "职位名称")
    private String title;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "工作城市")
    private String city;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "是否紧急")
    private Integer urgent;
}

