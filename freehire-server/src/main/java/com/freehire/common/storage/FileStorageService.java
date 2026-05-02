package com.freehire.common.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件存储服务接口
 * 策略模式：支持多种存储实现（本地、OSS、MinIO等）
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface FileStorageService {

    /**
     * 上传文件
     *
     * @param file   文件
     * @param folder 文件夹（如 resume、avatar）
     * @return 文件存储路径
     */
    String upload(MultipartFile file, String folder);

    /**
     * 上传文件（从输入流）
     *
     * @param inputStream 输入流
     * @param folder      文件夹
     * @param fileName    文件名
     * @param contentType 内容类型
     * @return 文件存储路径
     */
    String upload(InputStream inputStream, String folder, String fileName, String contentType);

    /**
     * 下载文件
     *
     * @param path 文件路径
     * @return 文件输入流
     */
    InputStream download(String path);

    /**
     * 删除文件
     *
     * @param path 文件路径
     */
    void delete(String path);

    /**
     * 获取文件访问URL
     *
     * @param path 文件路径
     * @return 访问URL
     */
    String getUrl(String path);

    /**
     * 判断文件是否存在
     *
     * @param path 文件路径
     * @return 是否存在
     */
    boolean exists(String path);

    /**
     * 获取存储类型
     *
     * @return 存储类型标识
     */
    String getStorageType();
}

