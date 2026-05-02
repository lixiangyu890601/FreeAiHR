package com.freehire.common.storage.impl;

import com.freehire.common.exception.BusinessException;
import com.freehire.common.storage.FileStorageProperties;
import com.freehire.common.storage.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 阿里云OSS文件存储实现（预留）
 * 
 * 使用时需要添加依赖：
 * <dependency>
 *     <groupId>com.aliyun.oss</groupId>
 *     <artifactId>aliyun-sdk-oss</artifactId>
 *     <version>3.17.4</version>
 * </dependency>
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
public class OssFileStorageService implements FileStorageService {

    private final FileStorageProperties.OssConfig config;

    public OssFileStorageService(FileStorageProperties.OssConfig config) {
        this.config = config;
        // TODO: 初始化 OSS Client
        // OSS ossClient = new OSSClientBuilder().build(config.getEndpoint(), 
        //         config.getAccessKeyId(), config.getAccessKeySecret());
        log.info("OSS存储服务初始化完成");
    }

    @Override
    public String upload(MultipartFile file, String folder) {
        // TODO: 实现OSS上传
        throw new BusinessException("OSS存储尚未实现，请切换到local存储类型");
    }

    @Override
    public String upload(InputStream inputStream, String folder, String fileName, String contentType) {
        // TODO: 实现OSS上传
        throw new BusinessException("OSS存储尚未实现，请切换到local存储类型");
    }

    @Override
    public InputStream download(String path) {
        // TODO: 实现OSS下载
        throw new BusinessException("OSS存储尚未实现，请切换到local存储类型");
    }

    @Override
    public void delete(String path) {
        // TODO: 实现OSS删除
        log.warn("OSS删除尚未实现: {}", path);
    }

    @Override
    public String getUrl(String path) {
        // 返回OSS访问URL
        if (config.getCustomDomain() != null && !config.getCustomDomain().isEmpty()) {
            return "https://" + config.getCustomDomain() + "/" + path;
        }
        return "https://" + config.getBucketName() + "." + config.getEndpoint() + "/" + path;
    }

    @Override
    public boolean exists(String path) {
        // TODO: 实现OSS文件存在检查
        return false;
    }

    @Override
    public String getStorageType() {
        return "oss";
    }
}

