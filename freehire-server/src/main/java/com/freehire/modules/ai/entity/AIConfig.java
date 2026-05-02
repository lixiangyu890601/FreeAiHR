package com.freehire.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI配置实体
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@TableName("ai_config")
public class AIConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 提供商 (openai/qwen/deepseek/zhipu)
     */
    private String provider;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * API地址
     */
    private String baseUrl;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 是否默认
     */
    private Integer isDefault;

    /**
     * 状态
     */
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

