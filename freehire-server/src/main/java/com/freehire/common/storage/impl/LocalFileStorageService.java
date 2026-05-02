package com.freehire.common.storage.impl;

import com.freehire.common.exception.BusinessException;
import com.freehire.common.response.ResultCode;
import com.freehire.common.storage.FileStorageProperties;
import com.freehire.common.storage.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地文件存储实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final FileStorageProperties.LocalConfig config;
    private final Path basePath;

    public LocalFileStorageService(FileStorageProperties.LocalConfig config) {
        this.config = config;
        this.basePath = Paths.get(config.getBasePath());
        initStorage();
    }

    /**
     * 初始化存储目录
     */
    private void initStorage() {
        try {
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
                log.info("创建文件存储目录: {}", basePath);
            }
        } catch (IOException e) {
            log.error("创建存储目录失败: {}", e.getMessage());
            throw new RuntimeException("无法创建文件存储目录: " + basePath, e);
        }
    }

    @Override
    public String upload(MultipartFile file, String folder) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String datePath = LocalDate.now().format(DATE_FORMATTER);
            String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
            String relativePath = folder + "/" + datePath + "/" + newFileName;

            Path targetPath = basePath.resolve(relativePath);
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("文件上传成功: {}", relativePath);
            return relativePath;
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public String upload(InputStream inputStream, String folder, String fileName, String contentType) {
        try {
            String extension = getFileExtension(fileName);
            String datePath = LocalDate.now().format(DATE_FORMATTER);
            String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
            String relativePath = folder + "/" + datePath + "/" + newFileName;

            Path targetPath = basePath.resolve(relativePath);
            Files.createDirectories(targetPath.getParent());
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("文件上传成功: {}", relativePath);
            return relativePath;
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public InputStream download(String path) {
        try {
            Path filePath = basePath.resolve(path);
            if (!Files.exists(filePath)) {
                throw new BusinessException(ResultCode.FILE_NOT_FOUND);
            }
            return new FileInputStream(filePath.toFile());
        } catch (FileNotFoundException e) {
            log.error("文件不存在: {}", path);
            throw new BusinessException(ResultCode.FILE_NOT_FOUND);
        }
    }

    @Override
    public void delete(String path) {
        try {
            Path filePath = basePath.resolve(path);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("文件删除成功: {}", path);
            }
        } catch (IOException e) {
            log.error("文件删除失败: {}", e.getMessage());
        }
    }

    @Override
    public String getUrl(String path) {
        // 返回相对URL，由Controller处理实际访问
        return config.getUrlPrefix() + "/" + path;
    }

    @Override
    public boolean exists(String path) {
        Path filePath = basePath.resolve(path);
        return Files.exists(filePath);
    }

    @Override
    public String getStorageType() {
        return "local";
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
     * 获取文件的完整路径（用于直接访问）
     */
    public Path getFullPath(String relativePath) {
        return basePath.resolve(relativePath);
    }
}

