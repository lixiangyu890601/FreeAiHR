package com.freehire.common.storage;

import com.freehire.common.storage.impl.LocalFileStorageService;
import com.freehire.common.storage.impl.MinioFileStorageService;
import com.freehire.common.storage.impl.OssFileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件存储自动装配配置
 * 根据配置自动选择存储实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
public class FileStorageAutoConfiguration {

    /**
     * 本地文件存储（默认）
     */
    @Bean
    @ConditionalOnProperty(name = "file.storage-type", havingValue = "local", matchIfMissing = true)
    public FileStorageService localFileStorageService(FileStorageProperties properties) {
        log.info("初始化本地文件存储服务，路径: {}", properties.getLocal().getBasePath());
        return new LocalFileStorageService(properties.getLocal());
    }

    /**
     * 阿里云OSS存储
     */
    @Bean
    @ConditionalOnProperty(name = "file.storage-type", havingValue = "oss")
    public FileStorageService ossFileStorageService(FileStorageProperties properties) {
        log.info("初始化阿里云OSS存储服务，Bucket: {}", properties.getOss().getBucketName());
        return new OssFileStorageService(properties.getOss());
    }

    /**
     * MinIO存储
     */
    @Bean
    @ConditionalOnProperty(name = "file.storage-type", havingValue = "minio")
    public FileStorageService minioFileStorageService(FileStorageProperties properties) {
        log.info("初始化MinIO存储服务，Endpoint: {}", properties.getMinio().getEndpoint());
        return new MinioFileStorageService(properties.getMinio());
    }
}

