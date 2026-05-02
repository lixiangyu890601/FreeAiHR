package com.freehire.modules.ai.service;

import com.freehire.modules.ai.dto.MatchResult;
import com.freehire.modules.ai.dto.ResumeParseResult;

import java.util.List;

/**
 * AI服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface AIService {

    /**
     * 简单对话
     */
    String chat(String prompt);

    /**
     * 解析简历
     */
    ResumeParseResult parseResume(String resumeText);

    /**
     * 简历-职位匹配
     */
    MatchResult matchResumeWithJob(String resumeText, String jobDescription);

    /**
     * 生成职位描述
     */
    String generateJobDescription(String title, String requirements);

    /**
     * 生成面试问题
     */
    List<String> generateInterviewQuestions(String jobTitle, String resumeText);

    /**
     * 测试AI连接
     */
    boolean testConnection(String provider);

    /**
     * 获取当前使用的AI提供商
     */
    String getCurrentProvider();
}

