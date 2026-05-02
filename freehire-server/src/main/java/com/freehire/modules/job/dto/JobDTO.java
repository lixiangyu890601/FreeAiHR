package com.freehire.modules.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 职位DTO（新增/编辑）
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@Schema(description = "职位DTO")
public class JobDTO {

    @Schema(description = "职位ID（编辑时需要）")
    private Long id;

    @Schema(description = "职位名称")
    @NotBlank(message = "职位名称不能为空")
    private String title;

    @Schema(description = "所属部门ID")
    private Long deptId;

    @Schema(description = "职位类型")
    private String jobType;

    @Schema(description = "工作城市")
    private String city;

    @Schema(description = "工作地址")
    private String address;

    @Schema(description = "最低薪资（K）")
    private Integer salaryMin;

    @Schema(description = "最高薪资（K）")
    private Integer salaryMax;

    @Schema(description = "薪资月数")
    private Integer salaryMonth;

    @Schema(description = "学历要求")
    private String education;

    @Schema(description = "经验要求")
    private String experience;

    @Schema(description = "招聘人数")
    private Integer headcount;

    @Schema(description = "职位描述")
    private String description;

    @Schema(description = "任职要求")
    private String requirements;

    @Schema(description = "职位亮点")
    private String highlights;

    @Schema(description = "是否紧急")
    private Integer urgent;

    @Schema(description = "截止日期")
    private LocalDate deadline;

    @Schema(description = "负责HR用户ID")
    private Long hrUserId;

    @Schema(description = "职位标签")
    private List<String> tags;
}

