package com.freehire.common.response;

import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Getter
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    FAILED(400, "操作失败"),
    VALIDATE_FAILED(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "资源不存在"),

    // 服务端错误 5xx
    ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // 业务错误 1xxx
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_DISABLED(1003, "用户已被禁用"),
    USER_EXIST(1004, "用户已存在"),
    
    // 职位相关 2xxx
    JOB_NOT_EXIST(2001, "职位不存在"),
    JOB_CLOSED(2002, "职位已关闭"),
    
    // 简历相关 3xxx
    RESUME_NOT_EXIST(3001, "简历不存在"),
    RESUME_PARSE_FAILED(3002, "简历解析失败"),
    RESUME_FORMAT_ERROR(3003, "简历格式不支持"),
    
    // 候选人相关 4xxx
    CANDIDATE_NOT_EXIST(4001, "候选人不存在"),
    CANDIDATE_STATUS_ERROR(4002, "候选人状态异常"),
    
    // AI服务相关 5xxx
    AI_SERVICE_ERROR(5001, "AI服务异常"),
    AI_KEY_INVALID(5002, "AI密钥无效"),
    AI_QUOTA_EXCEEDED(5003, "AI调用次数超限"),
    
    // 文件相关 6xxx
    FILE_UPLOAD_ERROR(6001, "文件上传失败"),
    FILE_NOT_FOUND(6002, "文件不存在"),
    FILE_TYPE_ERROR(6003, "文件类型不支持"),
    
    // 订阅/套餐相关 7xxx
    FEATURE_NOT_AVAILABLE(7001, "当前套餐不支持此功能"),
    QUOTA_EXCEEDED(7002, "用量已达上限"),
    SUBSCRIPTION_EXPIRED(7003, "订阅已过期"),
    LICENSE_INVALID(7004, "许可证无效");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 消息
     */
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

