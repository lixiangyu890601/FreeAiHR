package com.freehire.common.storage.impl;

import com.freehire.common.exception.BusinessException;
import com.freehire.common.storage.FileStorageProperties;
import com.freehire.common.storage.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * MinIO文件存储实现（预留）
 * 
 * 使用时需要添加依赖：
 * <dependency>
 *     <groupId>io.minio</groupId>
 *     <artifactId>minio</artifactId>
 *     <version>8.5.7</version>
 * </dependency>
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
public class MinioFileStorageService implements FileStorageService {

    private final FileStorageProperties.MinioConfig config;

    public MinioFileStorageService(FileStorageProperties.MinioConfig config) {
        this.config = config;
        // TODO: 初始化 MinIO Client
        // MinioClient minioClient = MinioClient.builder()
        //         .endpoint(config.getEndpoint())
        //         .credentials(config.getAccessKey(), config.getSecretKey())
        //         .build();
        log.info("MinIO存储服务初始化完成");
    }

    @Override
    public String upload(MultipartFile file, String folder) {
        // TODO: 实现MinIO上传
        throw new BusinessException("MinIO存储尚未实现，请切换到local存储类型");
    }

    @Override
    public String upload(InputStream inputStream, String folder, String fileName, String contentType) {
        // TODO: 实现MinIO上传
        throw new BusinessException("MinIO存储尚未实现，请切换到local存储类型");
    }

    @Override
    public InputStream download(String path) {
        // TODO: 实现MinIO下载
        throw new BusinessException("MinIO存储尚未实现，请切换到local存储类型");
    }

    @Override
    public void delete(String path) {
        // TODO: 实现MinIO删除
        log.warn("MinIO删除尚未实现: {}", path);
    }

    @Override
    public String getUrl(String path) {
        // 返回MinIO访问URL
        return config.getEndpoint() + "/" + config.getBucketName() + "/" + path;
    }

    @Override
    public boolean exists(String path) {
        // TODO: 实现MinIO文件存在检查
        return false;
    }

    @Override
    public String getStorageType() {
        return "minio";
    }
}

