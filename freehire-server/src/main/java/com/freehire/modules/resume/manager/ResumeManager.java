package com.freehire.modules.resume.manager;

import cn.hutool.json.JSONUtil;
import com.freehire.common.exception.BusinessException;
import com.freehire.common.manager.AIManager;
import com.freehire.common.manager.QuotaManager;
import com.freehire.common.response.ResultCode;
import com.freehire.common.service.FileService;
import com.freehire.modules.ai.dto.ResumeParseResult;
import com.freehire.modules.candidate.entity.Candidate;
import com.freehire.modules.candidate.mapper.CandidateMapper;
import com.freehire.modules.resume.entity.Resume;
import com.freehire.modules.resume.mapper.ResumeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.InputStream;

/**
 * 简历业务管理器
 * 处理简历解析、文本提取等复杂业务逻辑
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResumeManager {

    private final ResumeMapper resumeMapper;
    private final CandidateMapper candidateMapper;
    private final FileService fileService;
    private final AIManager aiManager;
    private final QuotaManager quotaManager;

    /**
     * 解析简历（完整流程）
     *
     * @param resume 简历实体
     * @return 解析后的简历
     */
    @Transactional
    public Resume parseAndSave(Resume resume) {
        // 1. 检查权限和配额
        quotaManager.requireFeature("ai_parse", "当前套餐不支持AI简历解析功能，请升级套餐");
        quotaManager.requireQuota("ai_parse", "本月AI解析次数已达上限，请升级套餐");

        // 2. 更新状态为解析中
        resume.setParseStatus("processing");
        resumeMapper.updateById(resume);

        try {
            // 3. 提取简历文本
            String resumeText = extractText(resume);
            if (!StringUtils.hasText(resumeText)) {
                throw new BusinessException(ResultCode.RESUME_PARSE_FAILED.getCode(), "无法提取简历文本");
            }
            resume.setRawText(resumeText);

            // 4. 调用AI解析
            ResumeParseResult result = aiManager.parseResume(resumeText);

            // 5. 填充解析结果
            fillParseResult(resume, result);

            // 6. 更新用量
            quotaManager.incrementUsage("ai_parse", 1);

            // 7. 保存简历
            resumeMapper.updateById(resume);
            
            // 8. 同步更新候选人信息（以简历解析结果为准）
            updateCandidateFromResume(resume);
            
            log.info("简历解析成功: {}", resume.getId());

            return resume;

        } catch (BusinessException e) {
            resume.setParseStatus("failed");
            resumeMapper.updateById(resume);
            throw e;
        } catch (Exception e) {
            log.error("简历解析失败: {}", e.getMessage(), e);
            resume.setParseStatus("failed");
            resumeMapper.updateById(resume);
            throw new BusinessException(ResultCode.RESUME_PARSE_FAILED);
        }
    }

    /**
     * 提取简历文本
     *
     * @param resume 简历实体
     * @return 提取的文本
     */
    public String extractText(Resume resume) {
        String fileType = resume.getFileType();
        
        if ("pdf".equalsIgnoreCase(fileType)) {
            return extractPdfText(resume.getFilePath());
        }
        
        // TODO: 支持更多格式 (doc, docx, 图片OCR)
        log.warn("暂不支持的文件类型: {}", fileType);
        return null;
    }

    /**
     * 提取PDF文本
     */
    private String extractPdfText(String filePath) {
        try (InputStream inputStream = fileService.getFileStream(filePath)) {
            // PDFBox 3.0+ 使用 Loader.loadPDF
            byte[] pdfBytes = inputStream.readAllBytes();
            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        } catch (Exception e) {
            log.error("PDF文本提取失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 填充解析结果到简历实体
     */
    private void fillParseResult(Resume resume, ResumeParseResult result) {
        resume.setParsedContent(JSONUtil.toJsonStr(result));

        // 使用 result 的便捷方法获取数据（会自动从 BasicInfo 或 educations 中获取）
        resume.setName(result.getName());
        resume.setPhone(result.getPhone());
        resume.setEmail(result.getEmail());
        resume.setGender(result.getGender());
        resume.setAge(result.getAge());
        resume.setCity(result.getCity());
        resume.setEducation(result.getEducation());
        resume.setSchool(result.getSchool());       // 使用便捷方法，会优先从 BasicInfo 获取
        resume.setMajor(result.getMajor());         // 使用便捷方法，会优先从 BasicInfo 获取
        resume.setWorkYears(result.getWorkYears());
        resume.setCurrentCompany(result.getCurrentCompany());
        resume.setCurrentPosition(result.getCurrentPosition());
        resume.setExpectPosition(result.getExpectPosition());
        resume.setExpectCity(result.getExpectCity());
        
        // 期望薪资
        if (result.getBasicInfo() != null && result.getBasicInfo().getExpectSalaryMin() != null) {
            resume.setExpectSalary(result.getBasicInfo().getExpectSalaryMin());
        }

        if (result.getSkills() != null && !result.getSkills().isEmpty()) {
            resume.setSkills(JSONUtil.toJsonStr(result.getSkills()));
        }

        resume.setParseStatus("success");
        resume.setParsed(1);
        
        log.info("简历解析结果填充完成: name={}, school={}, major={}", 
                resume.getName(), resume.getSchool(), resume.getMajor());
    }

    /**
     * 根据简历解析结果更新候选人信息
     */
    private void updateCandidateFromResume(Resume resume) {
        if (resume.getCandidateId() == null) {
            return;
        }
        
        Candidate candidate = candidateMapper.selectById(resume.getCandidateId());
        if (candidate == null) {
            return;
        }
        
        // 以简历解析结果为准更新候选人信息
        if (StringUtils.hasText(resume.getName())) {
            candidate.setName(resume.getName());
        }
        if (StringUtils.hasText(resume.getPhone())) {
            candidate.setPhone(resume.getPhone());
        }
        if (StringUtils.hasText(resume.getEmail())) {
            candidate.setEmail(resume.getEmail());
        }
        if (StringUtils.hasText(resume.getGender())) {
            candidate.setGender(parseGender(resume.getGender()));
        }
        if (resume.getAge() != null) {
            candidate.setAge(resume.getAge());
        }
        if (StringUtils.hasText(resume.getCity())) {
            candidate.setCity(resume.getCity());
        }
        if (StringUtils.hasText(resume.getEducation())) {
            candidate.setEducation(resume.getEducation());
        }
        if (StringUtils.hasText(resume.getSchool())) {
            candidate.setSchool(resume.getSchool());
        }
        if (StringUtils.hasText(resume.getMajor())) {
            candidate.setMajor(resume.getMajor());
        }
        if (resume.getWorkYears() != null) {
            candidate.setWorkYears(resume.getWorkYears());
        }
        if (StringUtils.hasText(resume.getCurrentCompany())) {
            candidate.setCurrentCompany(resume.getCurrentCompany());
        }
        if (StringUtils.hasText(resume.getCurrentPosition())) {
            candidate.setCurrentPosition(resume.getCurrentPosition());
        }
        if (StringUtils.hasText(resume.getExpectPosition())) {
            candidate.setExpectPosition(resume.getExpectPosition());
        }
        if (StringUtils.hasText(resume.getExpectCity())) {
            candidate.setExpectCity(resume.getExpectCity());
        }
        if (resume.getExpectSalary() != null) {
            candidate.setExpectSalary(resume.getExpectSalary());
        }
        if (StringUtils.hasText(resume.getSkills())) {
            candidate.setSkills(resume.getSkills());
        }
        
        candidateMapper.updateById(candidate);
        log.info("候选人信息已从简历同步更新: candidateId={}, name={}", candidate.getId(), candidate.getName());
    }

    /**
     * 解析性别
     */
    private Integer parseGender(String gender) {
        if (gender == null) return null;
        if (gender.contains("男")) return 1;
        if (gender.contains("女")) return 2;
        return null;
    }
}

