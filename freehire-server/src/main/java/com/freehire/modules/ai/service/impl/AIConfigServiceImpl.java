package com.freehire.modules.ai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.freehire.common.exception.BusinessException;
import com.freehire.modules.ai.dto.AIConfigDTO;
import com.freehire.modules.ai.entity.AIConfig;
import com.freehire.modules.ai.mapper.AIConfigMapper;
import com.freehire.modules.ai.provider.AIProvider;
import com.freehire.modules.ai.provider.AIProviderFactory;
import com.freehire.modules.ai.service.AIConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI配置服务实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIConfigServiceImpl implements AIConfigService {

    private final AIConfigMapper aiConfigMapper;
    private final AIProviderFactory aiProviderFactory;
    private final AIServiceImpl aiService;

    @Override
    public List<AIConfig> getAllConfigs() {
        return aiConfigMapper.selectList(new LambdaQueryWrapper<AIConfig>()
                .orderByDesc(AIConfig::getIsDefault)
                .orderByDesc(AIConfig::getCreateTime));
    }

    @Override
    public AIConfig getDefaultConfig() {
        return aiConfigMapper.selectDefault();
    }

    @Override
    public AIConfig getConfigById(Long id) {
        AIConfig config = aiConfigMapper.selectById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }
        return config;
    }

    @Override
    @Transactional
    public Long saveConfig(AIConfigDTO dto) {
        AIConfig config = new AIConfig();
        BeanUtil.copyProperties(dto, config);
        config.setUpdateTime(LocalDateTime.now());

        Long resultId;
        if (dto.getId() != null) {
            // 更新
            aiConfigMapper.updateById(config);
            log.info("更新AI配置: {}", dto.getId());
            resultId = dto.getId();
        } else {
            // 新增
            config.setCreateTime(LocalDateTime.now());
            if (config.getStatus() == null) {
                config.setStatus(1);
            }
            if (config.getIsDefault() == null) {
                config.setIsDefault(0);
            }
            // 如果是第一个配置，设为默认
            Long count = aiConfigMapper.selectCount(null);
            if (count == 0) {
                config.setIsDefault(1);
            }
            aiConfigMapper.insert(config);
            log.info("新增AI配置: {}", config.getId());
            resultId = config.getId();
        }
        
        // 刷新AI服务配置
        aiService.refreshConfig();
        
        return resultId;
    }

    @Override
    @Transactional
    public void deleteConfig(Long id) {
        AIConfig config = aiConfigMapper.selectById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }
        if (config.getIsDefault() == 1) {
            throw new BusinessException("不能删除默认配置");
        }
        aiConfigMapper.deleteById(id);
        log.info("删除AI配置: {}", id);
        
        // 刷新AI服务配置
        aiService.refreshConfig();
    }

    @Override
    @Transactional
    public void setDefault(Long id) {
        AIConfig config = aiConfigMapper.selectById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }

        // 取消其他默认
        aiConfigMapper.update(null, new LambdaUpdateWrapper<AIConfig>()
                .set(AIConfig::getIsDefault, 0)
                .eq(AIConfig::getIsDefault, 1));

        // 设置当前为默认
        config.setIsDefault(1);
        aiConfigMapper.updateById(config);
        log.info("设置默认AI配置: {}", id);
        
        // 刷新AI服务配置
        aiService.refreshConfig();
    }

    @Override
    public boolean testConnection(Long id) {
        AIConfig config = getConfigById(id);
        return testConnection(config.getProvider(), config.getApiKey(), config.getBaseUrl(), config.getModel());
    }

    @Override
    public boolean testConnection(String provider, String apiKey, String baseUrl, String model) {
        try {
            AIProvider aiProvider = aiProviderFactory.createProvider(provider, apiKey, baseUrl, model);
            return aiProvider.testConnection();
        } catch (Exception e) {
            log.error("AI连接测试失败: {}", e.getMessage());
            return false;
        }
    }
}

