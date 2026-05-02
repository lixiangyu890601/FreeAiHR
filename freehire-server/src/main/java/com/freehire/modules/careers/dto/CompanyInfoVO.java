package com.freehire.modules.careers.dto;

import lombok.Data;

import java.util.List;

/**
 * 公司信息VO（用于公开招聘页面展示）
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
public class CompanyInfoVO {

    /**
     * 公司名称
     */
    private String name;

    /**
     * 公司Logo URL
     */
    private String logo;

    /**
     * 公司简介
     */
    private String intro;

    /**
     * 公司福利标签
     */
    private List<String> benefits;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 公司地址
     */
    private String address;

    /**
     * 公司规模
     */
    private String scale;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 公司官网
     */
    private String website;
}

