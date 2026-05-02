package com.freehire.modules.resume.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.modules.resume.dto.ResumeQuery;
import com.freehire.modules.resume.entity.Resume;
import org.springframework.web.multipart.MultipartFile;

/**
 * 简历服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface ResumeService {

    /**
     * 分页查询简历
     */
    IPage<Resume> getResumePage(ResumeQuery query);

    /**
     * 获取简历详情
     */
    Resume getResumeById(Long id);

    /**
     * 上传简历
     */
    Resume uploadResume(MultipartFile file, String source);

    /**
     * 删除简历
     */
    void deleteResume(Long id);

    /**
     * AI解析简历
     */
    Resume parseResume(Long id);

    /**
     * 获取简历下载URL
     */
    String getResumeDownloadUrl(Long id);

    /**
     * 统计简历数量
     */
    int countResumes();
}

