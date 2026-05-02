package com.freehire.modules.ai.config;

import lombok.Data;

/**
 * AI服务配置属性
 * 仅作为数据传输对象使用，实际配置从数据库获取
 *
 * @author FreeHire
 * @since 1.0.0
 */
public class AIProperties {

    /**
     * AI提供商配置
     */
    @Data
    public static class ProviderConfig {
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
         * 超时时间（秒）
         */
        private Integer timeout = 120;

        /**
         * 最大Token数
         */
        private Integer maxTokens = 4096;

        /**
         * 温度参数
         */
        private Double temperature = 0.7;
    }
}
