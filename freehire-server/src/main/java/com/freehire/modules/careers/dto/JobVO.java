package com.freehire.modules.careers.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 职位信息VO（用于公开招聘页面展示）
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
public class JobVO {

    /**
     * 职位ID
     */
    private Long id;

    /**
     * 职位名称
     */
    private String title;

    /**
     * 所属部门
     */
    private String deptName;

    /**
     * 职位类型（full_time-全职 part_time-兼职 intern-实习）
     */
    private String jobType;

    /**
     * 职位类型名称
     */
    private String jobTypeName;

    /**
     * 工作城市
     */
    private String city;

    /**
     * 工作地址
     */
    private String address;

    /**
     * 薪资范围描述
     */
    private String salaryRange;

    /**
     * 学历要求
     */
    private String education;

    /**
     * 工作经验要求
     */
    private String experience;

    /**
     * 招聘人数
     */
    private Integer headcount;

    /**
     * 职位描述
     */
    private String description;

    /**
     * 任职要求
     */
    private String requirements;

    /**
     * 职位亮点/福利
     */
    private String highlights;

    /**
     * 职位标签
     */
    private List<String> tags;

    /**
     * 是否紧急
     */
    private Boolean urgent;

    /**
     * 发布日期
     */
    private LocalDate publishDate;

    /**
     * HR姓名
     */
    private String hrName;
}

