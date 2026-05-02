package com.freehire.common.controller;

import com.freehire.common.response.R;
import com.freehire.common.service.FileService;
import com.freehire.common.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件控制器
 * 处理文件上传和本地文件访问
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileStorageService fileStorageService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file,
                            @RequestParam(value = "folder", defaultValue = "common") String folder) {
        String path = fileService.uploadFile(file, folder);
        String url = fileService.getFileUrl(path);
        return R.ok(url);
    }

    /**
     * 访问本地文件
     * 当使用本地存储时，通过此接口访问文件
     */
    @GetMapping("/{folder}/**")
    public ResponseEntity<Resource> getFile(@PathVariable String folder,
                                            jakarta.servlet.http.HttpServletRequest request) {
        try {
            // 获取完整路径
            String requestUri = request.getRequestURI();
            String contextPath = request.getContextPath();
            String filePath = requestUri.substring(contextPath.length() + "/files/".length());

            // 只有本地存储才支持直接访问
            if (!"local".equals(fileStorageService.getStorageType())) {
                return ResponseEntity.notFound().build();
            }

            // 获取文件流
            InputStream inputStream = fileService.getFileStream(filePath);
            
            // 检测内容类型
            String contentType = detectContentType(filePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new InputStreamResource(inputStream));

        } catch (Exception e) {
            log.error("获取文件失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 下载文件
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("path") String path,
                                             @RequestParam(value = "filename", required = false) String filename) {
        try {
            InputStream inputStream = fileService.getFileStream(path);
            
            String downloadName = filename != null ? filename : getFileName(path);
            String encodedName = URLEncoder.encode(downloadName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename*=UTF-8''" + encodedName)
                    .body(new InputStreamResource(inputStream));

        } catch (Exception e) {
            log.error("下载文件失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping
    public R<Void> delete(@RequestParam("path") String path) {
        fileService.deleteFile(path);
        return R.ok();
    }

    /**
     * 检测文件内容类型
     */
    private String detectContentType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "txt" -> "text/plain";
            case "html" -> "text/html";
            case "json" -> "application/json";
            default -> "application/octet-stream";
        };
    }

    /**
     * 从路径中获取文件名
     */
    private String getFileName(String path) {
        int lastSlash = path.lastIndexOf("/");
        return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
    }
}

