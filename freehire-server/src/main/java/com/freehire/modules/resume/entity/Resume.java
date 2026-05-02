package com.freehire.modules.resume.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freehire.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 简历实体
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("resume")
public class Resume extends BaseEntity {

    /**
     * 候选人ID
     */
    private Long candidateId;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 文件路径（MinIO）
     */
    private String filePath;

    /**
     * 文件类型（pdf/doc/docx/jpg/png）
     */
    private String fileType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 是否已解析（0-否 1-是）
     */
    private Integer parsed;

    /**
     * 解析状态（pending-待解析 processing-解析中 success-成功 failed-失败）
     */
    private String parseStatus;

    /**
     * 解析后的结构化数据（JSON）
     */
    private String parsedContent;

    /**
     * 简历原文文本
     */
    private String rawText;

    // ========== 解析后的基本信息（冗余存储，方便查询） ==========

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
     * 性别
     */
    private String gender;

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
     * 简历来源（website-官网投递 upload-HR上传 email-邮件 referral-内推）
     */
    private String source;

    /**
     * 简历来源详情
     */
    private String sourceDetail;

    /**
     * 候选人姓名（非数据库字段，关联查询）
     */
    @TableField(exist = false)
    private String candidateName;
}

