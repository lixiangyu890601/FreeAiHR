package com.freehire.modules.candidate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 候选人DTO
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@Schema(description = "候选人DTO")
public class CandidateDTO {

    @Schema(description = "候选人ID")
    private Long id;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "出生日期")
    private LocalDate birthDate;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "现居城市")
    private String city;

    @Schema(description = "最高学历")
    private String education;

    @Schema(description = "毕业院校")
    private String school;

    @Schema(description = "专业")
    private String major;

    @Schema(description = "工作年限")
    private Integer workYears;

    @Schema(description = "当前公司")
    private String currentCompany;

    @Schema(description = "当前职位")
    private String currentPosition;

    @Schema(description = "期望职位")
    private String expectPosition;

    @Schema(description = "期望城市")
    private String expectCity;

    @Schema(description = "期望薪资（K）")
    private Integer expectSalary;

    @Schema(description = "技能列表")
    private List<String> skills;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "来源详情")
    private String sourceDetail;

    @Schema(description = "推荐人ID")
    private Long referrerId;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "备注")
    private String remark;
}

