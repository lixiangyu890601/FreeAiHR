package com.freehire.modules.system.controller;

import com.freehire.common.response.R;
import com.freehire.modules.system.entity.Config;
import com.freehire.modules.system.mapper.ConfigMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 公司配置管理
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Tag(name = "公司配置管理")
@RestController
@RequestMapping("/system/company")
@RequiredArgsConstructor
public class CompanyConfigController {

    private final ConfigMapper configMapper;

    // 公司配置键列表
    private static final String[] COMPANY_KEYS = {
            "company_name",
            "company_logo",
            "company_intro",
            "company_email",
            "company_address",
            "company_scale",
            "company_industry",
            "company_website",
            "company_benefits",
            "careers_page_enabled"
    };

    @Operation(summary = "获取公司配置")
    @GetMapping
    public R<Map<String, String>> getCompanyConfig() {
        Map<String, String> config = new HashMap<>();
        for (String key : COMPANY_KEYS) {
            String value = configMapper.getValueByKey(key);
            config.put(key, value != null ? value : "");
        }
        return R.ok(config);
    }

    @Operation(summary = "保存公司配置")
    @PostMapping
    public R<Void> saveCompanyConfig(@RequestBody Map<String, String> config) {
        for (Map.Entry<String, String> entry : config.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // 只允许保存公司配置键
            boolean isCompanyKey = false;
            for (String companyKey : COMPANY_KEYS) {
                if (companyKey.equals(key)) {
                    isCompanyKey = true;
                    break;
                }
            }
            if (!isCompanyKey) {
                continue;
            }
            
            saveConfig(key, value, "company");
        }
        log.info("公司配置已保存");
        return R.ok();
    }

    @Operation(summary = "获取招聘页面链接")
    @GetMapping("/careers-url")
    public R<String> getCareersUrl() {
        // 返回招聘页面的访问链接
        // 实际部署时可以从配置中读取域名
        return R.ok("/careers");
    }

    /**
     * 保存单个配置
     */
    private void saveConfig(String key, String value, String type) {
        Config existing = configMapper.selectByKey(key);
        if (existing != null) {
            existing.setConfigValue(value);
            existing.setUpdateTime(LocalDateTime.now());
            configMapper.updateById(existing);
        } else {
            Config config = new Config();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setConfigType(type);
            config.setCreateTime(LocalDateTime.now());
            config.setUpdateTime(LocalDateTime.now());
            configMapper.insert(config);
        }
    }
}

