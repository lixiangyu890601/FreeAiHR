package com.freehire.common.service;

import com.freehire.common.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件服务（门面类）
 * 委托给 FileStorageService 实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileStorageService fileStorageService;

    /**
     * 上传文件
     *
     * @param file   文件
     * @param folder 文件夹（如 resume、avatar）
     * @return 文件路径
     */
    public String uploadFile(MultipartFile file, String folder) {
        return fileStorageService.upload(file, folder);
    }

    /**
     * 获取文件访问URL
     *
     * @param path 文件路径
     * @return 访问URL
     */
    public String getFileUrl(String path) {
        return fileStorageService.getUrl(path);
    }

    /**
     * 获取文件流
     *
     * @param path 文件路径
     * @return 文件流
     */
    public InputStream getFileStream(String path) {
        return fileStorageService.download(path);
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     */
    public void deleteFile(String path) {
        fileStorageService.delete(path);
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件路径
     * @return 是否存在
     */
    public boolean exists(String path) {
        return fileStorageService.exists(path);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 验证文件类型
     */
    public boolean validateFileType(String filename, String... allowedTypes) {
        String extension = getFileExtension(filename);
        for (String type : allowedTypes) {
            if (type.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前存储类型
     */
    public String getStorageType() {
        return fileStorageService.getStorageType();
    }
}
