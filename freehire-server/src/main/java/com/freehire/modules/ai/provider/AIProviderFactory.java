package com.freehire.modules.ai.provider;

import com.freehire.common.exception.BusinessException;
import com.freehire.modules.ai.entity.AIConfig;
import com.freehire.modules.ai.mapper.AIConfigMapper;
import com.freehire.modules.ai.provider.impl.OpenAIProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * AI提供商工厂
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AIProviderFactory {

    private final AIConfigMapper aiConfigMapper;

    /**
     * 获取默认的AI提供商
     */
    public AIProvider getDefaultProvider() {
        AIConfig config = aiConfigMapper.selectDefault();
        if (config == null) {
            throw new BusinessException("未配置AI服务，请先在系统设置中配置AI服务");
        }
        return createProvider(config);
    }

    /**
     * 根据配置创建提供商
     */
    public AIProvider createProvider(AIConfig config) {
        return createProvider(config.getProvider(), config.getApiKey(), config.getBaseUrl(), config.getModel());
    }

    /**
     * 根据参数创建提供商
     * 所有服务商都兼容 OpenAI API 格式，统一使用 OpenAIProvider
     */
    public AIProvider createProvider(String provider, String apiKey, String baseUrl, String model) {
        // 所有支持的服务商都使用 OpenAI 兼容的 API 格式
        return new OpenAIProvider(apiKey, baseUrl, model);
    }
}

