package com.freehire.modules.ai.provider;

import com.freehire.modules.ai.dto.ChatMessage;
import com.freehire.modules.ai.dto.ResumeParseResult;
import com.freehire.modules.ai.dto.MatchResult;

import java.util.List;

/**
 * AI提供商接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface AIProvider {

    /**
     * 获取提供商名称
     */
    String getName();

    /**
     * 聊天完成
     *
     * @param messages 消息列表
     * @return AI响应
     */
    String chat(List<ChatMessage> messages);

    /**
     * 单轮对话
     *
     * @param prompt 提示词
     * @return AI响应
     */
    String chat(String prompt);

    /**
     * 解析简历
     *
     * @param resumeText 简历文本
     * @return 解析结果
     */
    ResumeParseResult parseResume(String resumeText);

    /**
     * 简历-职位匹配
     *
     * @param resumeText 简历文本
     * @param jobDescription 职位描述
     * @return 匹配结果
     */
    MatchResult matchResumeWithJob(String resumeText, String jobDescription);

    /**
     * 生成职位描述
     *
     * @param title 职位名称
     * @param requirements 基本要求
     * @return 生成的JD
     */
    String generateJobDescription(String title, String requirements);

    /**
     * 生成面试问题
     *
     * @param jobTitle 职位名称
     * @param resumeText 简历文本
     * @return 面试问题列表
     */
    List<String> generateInterviewQuestions(String jobTitle, String resumeText);

    /**
     * 测试连接
     *
     * @return 是否连接成功
     */
    boolean testConnection();
}

