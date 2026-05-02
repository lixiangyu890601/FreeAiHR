package com.freehire.common.manager;

import com.freehire.common.exception.BusinessException;
import com.freehire.common.response.ResultCode;
import com.freehire.modules.ai.dto.MatchResult;
import com.freehire.modules.ai.dto.ResumeParseResult;
import com.freehire.modules.ai.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI服务管理器
 * 统一封装AI服务调用，提供异常处理和默认行为
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AIManager {

    private final AIService aiService;

    /**
     * 解析简历
     *
     * @param resumeText 简历文本
     * @return 解析结果
     */
    public ResumeParseResult parseResume(String resumeText) {
        try {
            log.debug("开始解析简历，文本长度: {}", resumeText.length());
            ResumeParseResult result = aiService.parseResume(resumeText);
            log.info("简历解析完成");
            return result;
        } catch (Exception e) {
            log.error("AI简历解析失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR.getCode(), "AI简历解析失败: " + e.getMessage());
        }
    }

    /**
     * 匹配简历与职位
     *
     * @param resumeText     简历文本
     * @param jobDescription 职位描述
     * @return 匹配结果
     */
    public MatchResult matchResumeWithJob(String resumeText, String jobDescription) {
        try {
            log.debug("开始匹配简历与职位");
            MatchResult result = aiService.matchResumeWithJob(resumeText, jobDescription);
            log.info("简历匹配完成，匹配度: {}%", result.getScore());
            return result;
        } catch (Exception e) {
            log.error("AI匹配失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR.getCode(), "AI匹配失败: " + e.getMessage());
        }
    }

    /**
     * 生成职位描述
     *
     * @param title        职位名称
     * @param requirements 基本要求
     * @return 生成的职位描述
     */
    public String generateJobDescription(String title, String requirements) {
        try {
            log.debug("开始生成职位描述: {}", title);
            String jd = aiService.generateJobDescription(title, requirements);
            log.info("职位描述生成完成");
            return jd;
        } catch (Exception e) {
            log.error("AI生成JD失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR.getCode(), "AI生成职位描述失败: " + e.getMessage());
        }
    }

    /**
     * 生成面试问题
     *
     * @param jobTitle   职位名称
     * @param resumeText 简历文本
     * @return 面试问题列表
     */
    public List<String> generateInterviewQuestions(String jobTitle, String resumeText) {
        try {
            log.debug("开始生成面试问题，职位: {}", jobTitle);
            List<String> questions = aiService.generateInterviewQuestions(jobTitle, resumeText);
            log.info("生成了 {} 个面试问题", questions.size());
            return questions;
        } catch (Exception e) {
            log.error("AI生成面试问题失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR.getCode(), "AI生成面试问题失败: " + e.getMessage());
        }
    }

    /**
     * 通用聊天
     *
     * @param prompt 提示词
     * @return AI回复
     */
    public String chat(String prompt) {
        try {
            return aiService.chat(prompt);
        } catch (Exception e) {
            log.error("AI聊天失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR.getCode(), "AI服务暂时不可用");
        }
    }

    /**
     * 测试AI服务连接
     *
     * @param provider AI提供商
     * @return 是否连接成功
     */
    public boolean testConnection(String provider) {
        try {
            return aiService.testConnection(provider);
        } catch (Exception e) {
            log.error("AI连接测试失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取当前AI提供商
     */
    public String getCurrentProvider() {
        return aiService.getCurrentProvider();
    }
}

