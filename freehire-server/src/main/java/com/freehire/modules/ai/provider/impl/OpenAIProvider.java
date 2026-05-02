package com.freehire.modules.ai.provider.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.freehire.modules.ai.config.AIProperties;
import com.freehire.modules.ai.dto.ChatMessage;
import com.freehire.modules.ai.dto.MatchResult;
import com.freehire.modules.ai.dto.ResumeParseResult;
import com.freehire.modules.ai.provider.AIProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * OpenAI提供商实现（兼容OpenAI API格式）
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
public class OpenAIProvider implements AIProvider {

    private final String apiKey;
    private final String baseUrl;
    private final String model;

    public OpenAIProvider(String apiKey, String baseUrl, String model) {
        this.apiKey = apiKey;
        this.baseUrl = StringUtils.hasText(baseUrl) ? baseUrl : "https://api.openai.com/v1";
        this.model = StringUtils.hasText(model) ? model : "gpt-3.5-turbo";
    }

    /**
     * 使用ProviderConfig创建实例
     *
     * @param name   提供商名称（用于日志）
     * @param config 配置
     */
    public OpenAIProvider(String name, AIProperties.ProviderConfig config) {
        this.apiKey = config.getApiKey();
        this.baseUrl = StringUtils.hasText(config.getBaseUrl()) ? config.getBaseUrl() : "https://api.openai.com/v1";
        this.model = StringUtils.hasText(config.getModel()) ? config.getModel() : "gpt-3.5-turbo";
        log.info("初始化AI提供商: {}, baseUrl: {}, model: {}", name, this.baseUrl, this.model);
    }

    @Override
    public String getName() {
        return "OpenAI";
    }

    @Override
    public String chat(List<ChatMessage> messages) {
        try {
            JSONArray messagesArray = new JSONArray();
            for (ChatMessage msg : messages) {
                JSONObject msgObj = new JSONObject();
                msgObj.set("role", msg.getRole());
                msgObj.set("content", msg.getContent());
                messagesArray.add(msgObj);
            }

            JSONObject requestBody = new JSONObject();
            requestBody.set("model", model);
            requestBody.set("messages", messagesArray);
            requestBody.set("temperature", 0.7);

            String url = baseUrl + "/chat/completions";
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .timeout(60000)
                    .execute();

            if (response.isOk()) {
                JSONObject result = JSONUtil.parseObj(response.body());
                return result.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getStr("content");
            } else {
                log.error("OpenAI API调用失败: {}", response.body());
                throw new RuntimeException("AI调用失败: " + response.body());
            }
        } catch (Exception e) {
            log.error("OpenAI调用异常", e);
            throw new RuntimeException("AI调用异常: " + e.getMessage());
        }
    }

    @Override
    public String chat(String prompt) {
        ChatMessage message = new ChatMessage();
        message.setRole("user");
        message.setContent(prompt);
        return chat(Collections.singletonList(message));
    }

    @Override
    public ResumeParseResult parseResume(String resumeText) {
        String prompt = """
                请解析以下简历内容，提取关键信息并以JSON格式返回。
                
                返回格式：
                {
                    "name": "姓名",
                    "phone": "手机号",
                    "email": "邮箱",
                    "gender": "性别",
                    "age": 年龄数字,
                    "city": "现居城市",
                    "education": "最高学历",
                    "school": "毕业院校",
                    "major": "专业",
                    "workYears": 工作年限数字,
                    "currentCompany": "当前公司",
                    "currentPosition": "当前职位",
                    "expectPosition": "期望职位",
                    "expectCity": "期望城市",
                    "expectSalary": "期望薪资",
                    "skills": ["技能1", "技能2"],
                    "summary": "个人简介/总结"
                }
                
                简历内容：
                """ + resumeText + """
                
                只返回JSON，不要其他内容。
                """;

        String response = chat(prompt);
        try {
            // 提取JSON部分
            String json = extractJson(response);
            JSONObject obj = JSONUtil.parseObj(json);
            
            ResumeParseResult result = new ResumeParseResult();
            result.setName(obj.getStr("name"));
            result.setPhone(obj.getStr("phone"));
            result.setEmail(obj.getStr("email"));
            result.setGender(obj.getStr("gender"));
            result.setAge(obj.getInt("age"));
            result.setCity(obj.getStr("city"));
            result.setEducation(obj.getStr("education"));
            result.setSchool(obj.getStr("school"));
            result.setMajor(obj.getStr("major"));
            result.setWorkYears(obj.getInt("workYears"));
            result.setCurrentCompany(obj.getStr("currentCompany"));
            result.setCurrentPosition(obj.getStr("currentPosition"));
            result.setExpectPosition(obj.getStr("expectPosition"));
            result.setExpectCity(obj.getStr("expectCity"));
            result.setExpectSalary(obj.getStr("expectSalary"));
            result.setSkills(obj.getJSONArray("skills") != null ? 
                    obj.getJSONArray("skills").toList(String.class) : null);
            result.setSummary(obj.getStr("summary"));
            
            return result;
        } catch (Exception e) {
            log.error("简历解析结果转换失败", e);
            ResumeParseResult result = new ResumeParseResult();
            result.setSummary(response);
            return result;
        }
    }

    @Override
    public MatchResult matchResumeWithJob(String resumeText, String jobDescription) {
        String prompt = """
                请分析候选人简历与职位要求的匹配度，返回JSON格式的分析结果。
                
                职位要求：
                """ + jobDescription + """
                
                候选人简历：
                """ + resumeText + """
                
                返回格式：
                {
                    "overallScore": 综合匹配分数(0-100),
                    "skillScore": 技能匹配分数(0-100),
                    "experienceScore": 经验匹配分数(0-100),
                    "educationScore": 学历匹配分数(0-100),
                    "matchedSkills": ["匹配的技能1", "匹配的技能2"],
                    "missingSkills": ["缺失的技能1", "缺失的技能2"],
                    "strengths": ["优势1", "优势2"],
                    "weaknesses": ["不足1", "不足2"],
                    "summary": "综合评价（100字左右）"
                }
                
                只返回JSON，不要其他内容。
                """;

        String response = chat(prompt);
        try {
            String json = extractJson(response);
            JSONObject obj = JSONUtil.parseObj(json);
            
            MatchResult result = new MatchResult();
            result.setOverallScore(obj.getInt("overallScore", 60));
            result.setSkillScore(obj.getInt("skillScore", 60));
            result.setExperienceScore(obj.getInt("experienceScore", 60));
            result.setEducationScore(obj.getInt("educationScore", 60));
            result.setMatchedSkills(obj.getJSONArray("matchedSkills") != null ?
                    obj.getJSONArray("matchedSkills").toList(String.class) : new ArrayList<>());
            result.setMissingSkills(obj.getJSONArray("missingSkills") != null ?
                    obj.getJSONArray("missingSkills").toList(String.class) : new ArrayList<>());
            result.setStrengths(obj.getJSONArray("strengths") != null ?
                    obj.getJSONArray("strengths").toList(String.class) : new ArrayList<>());
            result.setWeaknesses(obj.getJSONArray("weaknesses") != null ?
                    obj.getJSONArray("weaknesses").toList(String.class) : new ArrayList<>());
            result.setSummary(obj.getStr("summary", ""));
            
            return result;
        } catch (Exception e) {
            log.error("匹配结果转换失败", e);
            MatchResult result = new MatchResult();
            result.setOverallScore(60);
            result.setSummary(response);
            return result;
        }
    }

    @Override
    public String generateJobDescription(String title, String requirements) {
        String prompt = """
                请为以下职位生成一份专业的职位描述（JD）。
                
                职位名称：""" + title + """
                
                基本要求：""" + requirements + """
                
                请生成包含以下内容的职位描述：
                1. 职位概述
                2. 工作职责（5-8条）
                3. 任职要求（5-8条）
                4. 加分项（2-3条）
                
                用中文输出，格式清晰。
                """;

        return chat(prompt);
    }

    @Override
    public List<String> generateInterviewQuestions(String jobTitle, String resumeText) {
        String prompt = """
                请根据以下职位和候选人简历，生成10个有针对性的面试问题。
                
                职位：""" + jobTitle + """
                
                候选人简历：
                """ + resumeText + """
                
                要求：
                1. 前3个问题考察专业技能
                2. 中间3个问题考察项目经验
                3. 后2个问题考察软技能
                4. 最后2个问题是开放性问题
                
                直接返回问题列表，每个问题一行，不要序号。
                """;

        String response = chat(prompt);
        List<String> questions = new ArrayList<>();
        for (String line : response.split("\n")) {
            line = line.trim();
            // 去除可能的序号
            line = line.replaceAll("^\\d+[\\.、)）]\\s*", "");
            if (!line.isEmpty()) {
                questions.add(line);
            }
        }
        return questions;
    }

    @Override
    public boolean testConnection() {
        try {
            String response = chat("你好，请回复'连接成功'");
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            log.error("AI连接测试失败", e);
            return false;
        }
    }

    private String extractJson(String text) {
        // 尝试提取JSON部分
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }
}
