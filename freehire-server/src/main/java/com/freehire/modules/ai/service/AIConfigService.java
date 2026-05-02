package com.freehire.modules.ai.service;

import com.freehire.modules.ai.dto.AIConfigDTO;
import com.freehire.modules.ai.entity.AIConfig;

import java.util.List;

/**
 * AI配置服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface AIConfigService {

    /**
     * 获取所有配置
     */
    List<AIConfig> getAllConfigs();

    /**
     * 获取默认配置
     */
    AIConfig getDefaultConfig();

    /**
     * 获取配置详情
     */
    AIConfig getConfigById(Long id);

    /**
     * 保存配置
     */
    Long saveConfig(AIConfigDTO dto);

    /**
     * 删除配置
     */
    void deleteConfig(Long id);

    /**
     * 设为默认
     */
    void setDefault(Long id);

    /**
     * 测试连接
     */
    boolean testConnection(Long id);

    /**
     * 测试连接（直接用参数）
     */
    boolean testConnection(String provider, String apiKey, String baseUrl, String model);
}

