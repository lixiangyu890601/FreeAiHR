package com.freehire.modules.ai.controller;

import com.freehire.common.response.R;
import com.freehire.modules.ai.config.AIProviderEnum;
import com.freehire.modules.ai.dto.AIConfigDTO;
import com.freehire.modules.ai.entity.AIConfig;
import com.freehire.modules.ai.service.AIConfigService;
import com.freehire.modules.ai.service.impl.AIServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI配置控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "AI配置管理")
@RestController
@RequestMapping("/ai/config")
@RequiredArgsConstructor
public class AIConfigController {

    private final AIConfigService aiConfigService;
    private final AIServiceImpl aiService;

    @Operation(summary = "获取所有AI配置")
    @GetMapping("/list")
    public R<List<AIConfig>> getAllConfigs() {
        return R.ok(aiConfigService.getAllConfigs());
    }

    @Operation(summary = "获取配置详情")
    @GetMapping("/{id}")
    public R<AIConfig> getConfigById(@PathVariable Long id) {
        return R.ok(aiConfigService.getConfigById(id));
    }

    @Operation(summary = "保存配置")
    @PostMapping
    public R<Long> saveConfig(@Valid @RequestBody AIConfigDTO dto) {
        return R.ok(aiConfigService.saveConfig(dto));
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/{id}")
    public R<Void> deleteConfig(@PathVariable Long id) {
        aiConfigService.deleteConfig(id);
        return R.ok();
    }

    @Operation(summary = "设为默认")
    @PostMapping("/{id}/set-default")
    public R<Void> setDefault(@PathVariable Long id) {
        aiConfigService.setDefault(id);
        return R.ok();
    }

    @Operation(summary = "测试连接")
    @PostMapping("/{id}/test")
    public R<Boolean> testConnection(@PathVariable Long id) {
        return R.ok(aiConfigService.testConnection(id));
    }

    @Operation(summary = "测试连接（参数）")
    @PostMapping("/test")
    public R<Boolean> testConnectionByParams(@RequestBody Map<String, String> params) {
        return R.ok(aiConfigService.testConnection(
                params.get("provider"),
                params.get("apiKey"),
                params.get("baseUrl"),
                params.get("model")
        ));
    }

    @Operation(summary = "获取支持的AI服务商列表")
    @GetMapping("/providers")
    public R<List<Map<String, String>>> getProviders() {
        List<Map<String, String>> providers = new ArrayList<>();
        for (AIProviderEnum provider : AIProviderEnum.values()) {
            Map<String, String> item = new HashMap<>();
            item.put("value", provider.getCode());
            item.put("label", provider.getName());
            item.put("defaultUrl", provider.getDefaultBaseUrl());
            item.put("defaultModel", provider.getDefaultModel());
            item.put("description", provider.getDescription());
            providers.add(item);
        }
        return R.ok(providers);
    }

    @Operation(summary = "获取AI服务状态")
    @GetMapping("/status")
    public R<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("configured", aiService.isConfigured());
        status.put("currentProvider", aiService.getCurrentProvider());
        if (!aiService.isConfigured()) {
            status.put("message", "AI服务未配置，请添加AI服务配置后使用AI功能");
        }
        return R.ok(status);
    }
}

