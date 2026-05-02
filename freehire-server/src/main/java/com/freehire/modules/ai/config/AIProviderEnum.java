package com.freehire.modules.ai.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI服务商枚举
 * 定义支持的AI服务商及其默认配置
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum AIProviderEnum {

    OPENAI("openai", "OpenAI", "https://api.openai.com/v1", "gpt-4o-mini", "适用于海外环境，需要代理"),
    
    KIMI("kimi", "Kimi (月之暗面)", "https://api.moonshot.cn/v1", "moonshot-v1-8k", "国内可用，性价比高"),
    
    DEEPSEEK("deepseek", "DeepSeek (深度求索)", "https://api.deepseek.com", "deepseek-chat", "国内可用，价格便宜"),
    
    QWEN("qwen", "通义千问 (阿里)", "https://dashscope.aliyuncs.com/compatible-mode/v1", "qwen-plus", "阿里云，稳定可靠"),
    
    DOUBAO("doubao", "豆包 (字节跳动)", "https://ark.cn-beijing.volces.com/api/v3", "doubao-pro-32k", "字节跳动，需要开通火山引擎"),
    
    ZHIPU("zhipu", "智谱AI (清华)", "https://open.bigmodel.cn/api/paas/v4", "glm-4-flash", "清华系，免费额度多"),
    
    BAICHUAN("baichuan", "百川智能", "https://api.baichuan-ai.com/v1", "Baichuan4", "国内可用"),
    
    MINIMAX("minimax", "MiniMax", "https://api.minimax.chat/v1", "abab6.5-chat", "国内可用"),
    
    OLLAMA("ollama", "Ollama (本地)", "http://localhost:11434/v1", "llama3", "本地部署，免费使用");

    /**
     * 服务商代码
     */
    private final String code;

    /**
     * 服务商名称
     */
    private final String name;

    /**
     * 默认API地址
     */
    private final String defaultBaseUrl;

    /**
     * 默认模型
     */
    private final String defaultModel;

    /**
     * 说明
     */
    private final String description;

    /**
     * 根据代码获取枚举
     */
    public static AIProviderEnum getByCode(String code) {
        for (AIProviderEnum provider : values()) {
            if (provider.getCode().equals(code)) {
                return provider;
            }
        }
        return null;
    }
}

