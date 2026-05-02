package com.freehire.modules.interview.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freehire.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 面试实体
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("interview")
public class Interview extends BaseEntity {

    /**
     * 申请ID
     */
    private Long applicationId;

    /**
     * 候选人ID
     */
    private Long candidateId;

    /**
     * 职位ID
     */
    private Long jobId;

    /**
     * 面试轮次
     */
    private Integer round;

    /**
     * 面试类型（onsite-现场 phone-电话 video-视频）
     */
    private String interviewType;

    /**
     * 面试时间
     */
    private LocalDateTime interviewTime;

    /**
     * 面试时长（分钟）
     */
    private Integer duration;

    /**
     * 面试地点
     */
    private String location;

    /**
     * 会议链接
     */
    private String meetingLink;

    /**
     * 面试官ID列表（逗号分隔）
     */
    private String interviewerIds;

    /**
     * 状态（scheduled-已安排 ongoing-进行中 completed-已完成 cancelled-已取消）
     */
    private String status;

    /**
     * 面试反馈
     */
    private String feedback;

    /**
     * 评分
     */
    private Integer score;

    /**
     * 结果（pass-通过 fail-不通过 pending-待定）
     */
    private String result;

    /**
     * 备注
     */
    private String remark;

    // ========== 非数据库字段 ==========

    /**
     * 候选人姓名
     */
    @TableField(exist = false)
    private String candidateName;

    /**
     * 职位名称
     */
    @TableField(exist = false)
    private String jobTitle;

    /**
     * 面试官姓名列表
     */
    @TableField(exist = false)
    private List<String> interviewerNames;
}

