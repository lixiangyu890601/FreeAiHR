package com.freehire.common.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件存储配置属性
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

    /**
     * 存储类型：local, oss, minio
     */
    private String storageType = "local";

    /**
     * 本地存储配置
     */
    private LocalConfig local = new LocalConfig();

    /**
     * 阿里云OSS配置
     */
    private OssConfig oss = new OssConfig();

    /**
     * MinIO配置
     */
    private MinioConfig minio = new MinioConfig();

    /**
     * 本地存储配置
     */
    @Data
    public static class LocalConfig {
        /**
         * 存储基础路径
         */
        private String basePath = "/data/freehire/uploads";

        /**
         * URL访问前缀
         */
        private String urlPrefix = "/api/files";

        /**
         * 允许的文件类型（逗号分隔）
         */
        private String allowedTypes = "pdf,doc,docx,jpg,jpeg,png,gif,xlsx,xls";

        /**
         * 最大文件大小（MB）
         */
        private int maxFileSize = 50;
    }

    /**
     * 阿里云OSS配置
     */
    @Data
    public static class OssConfig {
        /**
         * Endpoint
         */
        private String endpoint;

        /**
         * Access Key ID
         */
        private String accessKeyId;

        /**
         * Access Key Secret
         */
        private String accessKeySecret;

        /**
         * Bucket名称
         */
        private String bucketName;

        /**
         * 自定义域名（可选）
         */
        private String customDomain;
    }

    /**
     * MinIO配置
     */
    @Data
    public static class MinioConfig {
        /**
         * Endpoint
         */
        private String endpoint;

        /**
         * Access Key
         */
        private String accessKey;

        /**
         * Secret Key
         */
        private String secretKey;

        /**
         * Bucket名称
         */
        private String bucketName;
    }
}

