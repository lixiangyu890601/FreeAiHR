package com.freehire.modules.careers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 求职投递DTO
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
public class ApplyDTO {

    /**
     * 职位ID
     */
    @NotNull(message = "职位ID不能为空")
    private Long jobId;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 简历文件路径（上传后返回）
     */
    private String resumePath;

    /**
     * 简历文件名
     */
    private String resumeFileName;

    /**
     * 求职者留言
     */
    private String message;
}

