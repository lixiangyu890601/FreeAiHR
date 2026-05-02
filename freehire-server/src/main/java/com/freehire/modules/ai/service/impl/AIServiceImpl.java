package com.freehire.modules.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freehire.common.exception.BusinessException;
import com.freehire.common.response.ResultCode;
import com.freehire.modules.ai.config.AIProperties;
import com.freehire.modules.ai.dto.MatchResult;
import com.freehire.modules.ai.dto.ResumeParseResult;
import com.freehire.modules.ai.entity.AIConfig;
import com.freehire.modules.ai.mapper.AIConfigMapper;
import com.freehire.modules.ai.provider.AIProvider;
import com.freehire.modules.ai.provider.impl.OpenAIProvider;
import com.freehire.modules.ai.service.AIService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI服务实现
 * 所有AI配置从数据库获取，不再使用配置文件
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final AIConfigMapper aiConfigMapper;
    
    private final Map<String, AIProvider> providers = new HashMap<>();
    private String currentProvider;

    @PostConstruct
    public void init() {
        refreshConfig();
    }

    /**
     * 刷新AI配置（从数据库重新加载）
     */
    public void refreshConfig() {
        providers.clear();
        currentProvider = null;
        
        try {
            List<AIConfig> configs = aiConfigMapper.selectList(
                    new LambdaQueryWrapper<AIConfig>().eq(AIConfig::getStatus, 1));
            
            for (AIConfig config : configs) {
                if (StringUtils.hasText(config.getApiKey())) {
                    AIProperties.ProviderConfig providerConfig = new AIProperties.ProviderConfig();
                    providerConfig.setApiKey(config.getApiKey());
                    providerConfig.setBaseUrl(config.getBaseUrl());
                    providerConfig.setModel(config.getModel());
                    
                    // 所有服务商都使用 OpenAI 兼容的 API 格式
                    providers.put(config.getProvider(), new OpenAIProvider(config.getProvider(), providerConfig));
                    
                    if (config.getIsDefault() != null && config.getIsDefault() == 1) {
                        currentProvider = config.getProvider();
                    }
                }
            }
            
            // 如果没有设置默认，使用第一个可用的
            if (currentProvider == null && !providers.isEmpty()) {
                currentProvider = providers.keySet().iterator().next();
            }
            
            if (providers.isEmpty()) {
                log.warn("未配置任何AI服务，请在「系统管理 → AI配置」中添加配置");
            } else {
                log.info("AI服务加载完成，当前使用: {}, 可用服务商: {}", currentProvider, providers.keySet());
            }
        } catch (Exception e) {
            log.error("加载AI配置失败: {}", e.getMessage());
        }
    }

    /**
     * 获取当前AI提供商
     * 如果未配置，抛出友好的提示
     */
    private AIProvider getProvider() {
        if (providers.isEmpty()) {
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR.getCode(), 
                    "AI服务未配置，请先在「系统管理 → AI配置」中添加AI服务配置");
        }
        
        AIProvider provider = providers.get(currentProvider);
        if (provider == null) {
            provider = providers.values().iterator().next();
        }
        return provider;
    }

    /**
     * 检查AI服务是否已配置
     */
    public boolean isConfigured() {
        return !providers.isEmpty();
    }

    @Override
    public String chat(String prompt) {
        return getProvider().chat(prompt);
    }

    @Override
    public ResumeParseResult parseResume(String resumeText) {
        return getProvider().parseResume(resumeText);
    }

    @Override
    public MatchResult matchResumeWithJob(String resumeText, String jobDescription) {
        return getProvider().matchResumeWithJob(resumeText, jobDescription);
    }

    @Override
    public String generateJobDescription(String title, String requirements) {
        return getProvider().generateJobDescription(title, requirements);
    }

    @Override
    public List<String> generateInterviewQuestions(String jobTitle, String resumeText) {
        return getProvider().generateInterviewQuestions(jobTitle, resumeText);
    }

    @Override
    public boolean testConnection(String provider) {
        AIProvider p = providers.get(provider);
        if (p == null) {
            return false;
        }
        return p.testConnection();
    }

    @Override
    public String getCurrentProvider() {
        return currentProvider;
    }
}
