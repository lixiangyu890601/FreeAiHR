package com.freehire.modules.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI配置DTO
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
@Schema(description = "AI配置DTO")
public class AIConfigDTO {

    @Schema(description = "配置ID（编辑时需要）")
    private Long id;

    @Schema(description = "提供商")
    @NotBlank(message = "提供商不能为空")
    private String provider;

    @Schema(description = "API密钥")
    @NotBlank(message = "API密钥不能为空")
    private String apiKey;

    @Schema(description = "API地址")
    private String baseUrl;

    @Schema(description = "模型名称")
    @NotBlank(message = "模型名称不能为空")
    private String model;

    @Schema(description = "是否默认")
    private Integer isDefault;

    @Schema(description = "状态")
    private Integer status;
}

