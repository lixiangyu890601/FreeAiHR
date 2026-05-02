package com.freehire.modules.resume.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freehire.common.exception.BusinessException;
import com.freehire.common.manager.QuotaManager;
import com.freehire.common.response.ResultCode;
import com.freehire.common.service.FileService;
import com.freehire.modules.resume.dto.ResumeQuery;
import com.freehire.modules.resume.entity.Resume;
import com.freehire.modules.resume.manager.ResumeManager;
import com.freehire.modules.resume.mapper.ResumeMapper;
import com.freehire.modules.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 简历服务实现
 * 简化版：复杂业务逻辑委托给 ResumeManager
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;
    private final FileService fileService;
    private final QuotaManager quotaManager;
    private final ResumeManager resumeManager;

    // 允许的文件类型
    private static final List<String> ALLOWED_TYPES = Arrays.asList("pdf", "doc", "docx", "jpg", "jpeg", "png");

    @Override
    public IPage<Resume> getResumePage(ResumeQuery query) {
        Page<Resume> page = new Page<>(query.getCurrent(), query.getSize());

        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getName()), Resume::getName, query.getName())
               .like(StringUtils.hasText(query.getPhone()), Resume::getPhone, query.getPhone())
               .like(StringUtils.hasText(query.getEmail()), Resume::getEmail, query.getEmail())
               .eq(StringUtils.hasText(query.getParseStatus()), Resume::getParseStatus, query.getParseStatus())
               .eq(StringUtils.hasText(query.getSource()), Resume::getSource, query.getSource())
               .eq(StringUtils.hasText(query.getEducation()), Resume::getEducation, query.getEducation())
               .orderByDesc(Resume::getCreateTime);

        return resumeMapper.selectPage(page, wrapper);
    }

    @Override
    public Resume getResumeById(Long id) {
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            throw new BusinessException(ResultCode.RESUME_NOT_EXIST);
        }
        return resume;
    }

    @Override
    @Transactional
    public Resume uploadResume(MultipartFile file, String source) {
        // 检查配额
        quotaManager.requireQuota("resume", "简历数量已达上限，请升级套餐");

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (!fileService.validateFileType(originalFilename, ALLOWED_TYPES.toArray(new String[0]))) {
            throw new BusinessException(ResultCode.FILE_TYPE_ERROR.getCode(), 
                    "不支持的文件类型，请上传 " + String.join("/", ALLOWED_TYPES) + " 格式的文件");
        }

        // 上传文件
        String filePath = fileService.uploadFile(file, "resume");

        // 保存简历记录
        Resume resume = new Resume();
        resume.setFileName(originalFilename);
        resume.setFilePath(filePath);
        resume.setFileType(getFileExtension(originalFilename));
        resume.setFileSize(file.getSize());
        resume.setParseStatus("pending");
        resume.setSource(source != null ? source : "upload");

        if (StpUtil.isLogin()) {
            resume.setCreateBy(StpUtil.getLoginIdAsLong());
        }

        resumeMapper.insert(resume);

        // 更新用量统计
        quotaManager.incrementUsage("resume", 1);

        log.info("简历上传成功: {}", resume.getId());
        return resume;
    }

    @Override
    @Transactional
    public void deleteResume(Long id) {
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            throw new BusinessException(ResultCode.RESUME_NOT_EXIST);
        }

        // 删除文件
        if (StringUtils.hasText(resume.getFilePath())) {
            fileService.deleteFile(resume.getFilePath());
        }

        // 删除记录
        resumeMapper.deleteById(id);

        // 减少用量统计
        quotaManager.incrementUsage("resume", -1);

        log.info("简历删除成功: {}", id);
    }

    @Override
    @Transactional
    public Resume parseResume(Long id) {
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            throw new BusinessException(ResultCode.RESUME_NOT_EXIST);
        }
        
        // 委托给 Manager 处理复杂的解析逻辑
        return resumeManager.parseAndSave(resume);
    }

    @Override
    public String getResumeDownloadUrl(Long id) {
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            throw new BusinessException(ResultCode.RESUME_NOT_EXIST);
        }
        return fileService.getFileUrl(resume.getFilePath());
    }

    @Override
    public int countResumes() {
        return resumeMapper.countResumes();
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
