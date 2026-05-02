package com.freehire.modules.candidate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freehire.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 候选人实体
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("candidate")
public class Candidate extends BaseEntity {

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别（0-未知 1-男 2-女）
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 现居城市
     */
    private String city;

    /**
     * 最高学历
     */
    private String education;

    /**
     * 毕业院校
     */
    private String school;

    /**
     * 专业
     */
    private String major;

    /**
     * 工作年限
     */
    private Integer workYears;

    /**
     * 当前/最近公司
     */
    private String currentCompany;

    /**
     * 当前/最近职位
     */
    private String currentPosition;

    /**
     * 期望职位
     */
    private String expectPosition;

    /**
     * 期望城市
     */
    private String expectCity;

    /**
     * 期望薪资（K）
     */
    private Integer expectSalary;

    /**
     * 技能标签（JSON数组）
     */
    private String skills;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 最新简历ID
     */
    private Long latestResumeId;

    /**
     * 来源（website-官网 upload-上传 email-邮件 referral-内推 boss-BOSS直聘 lagou-拉勾 ...）
     */
    private String source;

    /**
     * 来源详情
     */
    private String sourceDetail;

    /**
     * 推荐人ID（内推场景）
     */
    private Long referrerId;

    /**
     * 标签（JSON数组）
     */
    private String tags;

    /**
     * 备注
     */
    private String remark;

    // ========== 非数据库字段 ==========

    /**
     * 推荐人姓名
     */
    @TableField(exist = false)
    private String referrerName;
}

