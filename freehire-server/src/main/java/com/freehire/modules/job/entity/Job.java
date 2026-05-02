package com.freehire.modules.job.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freehire.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * 职位实体
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("job_position")
public class Job extends BaseEntity {

    /**
     * 职位名称
     */
    private String title;

    /**
     * 所属部门ID
     */
    private Long deptId;

    /**
     * 职位类型（full_time-全职 part_time-兼职 intern-实习）
     */
    private String jobType;

    /**
     * 工作城市
     */
    private String city;

    /**
     * 工作地址
     */
    private String address;

    /**
     * 最低薪资（K）
     */
    private Integer salaryMin;

    /**
     * 最高薪资（K）
     */
    private Integer salaryMax;

    /**
     * 薪资月数
     */
    private Integer salaryMonth;

    /**
     * 学历要求
     */
    private String education;

    /**
     * 工作经验（年）
     */
    private String experience;

    /**
     * 招聘人数
     */
    private Integer headcount;

    /**
     * 职位描述/职责
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
     * 职位状态（0-关闭 1-招聘中 2-暂停）
     */
    private Integer status;

    /**
     * 是否紧急（0-否 1-是）
     */
    private Integer urgent;

    /**
     * 发布日期
     */
    private LocalDate publishDate;

    /**
     * 截止日期
     */
    private LocalDate deadline;

    /**
     * 负责HR用户ID
     */
    private Long hrUserId;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 投递数量
     */
    private Integer applyCount;

    /**
     * 职位标签（JSON数组）
     */
    private String tags;

    /**
     * 部门名称（非数据库字段）
     */
    @TableField(exist = false)
    private String deptName;

    /**
     * HR姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String hrName;

    /**
     * 标签列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<String> tagList;
}

