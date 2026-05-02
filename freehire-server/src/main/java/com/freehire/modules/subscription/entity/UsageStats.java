package com.freehire.modules.subscription.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用量统计实体（按月统计）
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@TableName("usage_stats")
public class UsageStats implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 统计月份 (格式: 2024-01)
     */
    private String statMonth;

    /**
     * 职位数量
     */
    private Integer jobCount;

    /**
     * 简历数量
     */
    private Integer resumeCount;

    /**
     * 候选人数量
     */
    private Integer candidateCount;

    /**
     * 用户数量
     */
    private Integer userCount;

    /**
     * AI解析次数
     */
    private Integer aiParseCount;

    /**
     * AI匹配次数
     */
    private Integer aiMatchCount;

    /**
     * AI生成次数
     */
    private Integer aiGenerateCount;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

