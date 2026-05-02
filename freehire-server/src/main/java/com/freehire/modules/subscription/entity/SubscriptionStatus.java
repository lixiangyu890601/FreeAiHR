package com.freehire.modules.subscription.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统订阅状态实体（单租户模式，只有一条记录）
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@TableName("subscription_status")
public class SubscriptionStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 当前套餐ID
     */
    private Long planId;

    /**
     * 许可证密钥
     */
    private String licenseKey;

    /**
     * 激活时间
     */
    private LocalDateTime activatedAt;

    /**
     * 过期时间
     */
    private LocalDateTime expiresAt;

    /**
     * 状态 (0-过期 1-正常 2-试用)
     */
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

