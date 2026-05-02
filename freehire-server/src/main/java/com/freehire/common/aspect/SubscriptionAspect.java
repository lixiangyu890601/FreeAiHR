package com.freehire.common.aspect;

import com.freehire.common.annotation.CheckQuota;
import com.freehire.common.annotation.RequireFeature;
import com.freehire.common.exception.BusinessException;
import com.freehire.common.response.ResultCode;
import com.freehire.modules.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 订阅/套餐权限切面
 * 处理 @RequireFeature 和 @CheckQuota 注解
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SubscriptionAspect {

    private final SubscriptionService subscriptionService;

    /**
     * 检查功能权限
     */
    @Before("@annotation(com.freehire.common.annotation.RequireFeature)")
    public void checkFeature(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequireFeature annotation = signature.getMethod().getAnnotation(RequireFeature.class);
        
        String featureCode = annotation.value();
        String featureName = annotation.name().isEmpty() ? getFeatureName(featureCode) : annotation.name();

        if (!subscriptionService.hasFeature(featureCode)) {
            log.warn("功能权限不足: {}", featureCode);
            throw new BusinessException(ResultCode.FEATURE_NOT_AVAILABLE.getCode(), 
                    "当前套餐不支持「" + featureName + "」功能，请升级套餐");
        }
    }

    /**
     * 检查用量限制
     */
    @Before("@annotation(com.freehire.common.annotation.CheckQuota)")
    public void checkQuota(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        CheckQuota annotation = signature.getMethod().getAnnotation(CheckQuota.class);
        
        String quotaType = annotation.value();
        String quotaName = annotation.name().isEmpty() ? getQuotaName(quotaType) : annotation.name();

        if (!subscriptionService.checkQuota(quotaType)) {
            log.warn("用量超限: {}", quotaType);
            throw new BusinessException(ResultCode.QUOTA_EXCEEDED.getCode(), 
                    "「" + quotaName + "」已达上限，请升级套餐以获取更多配额");
        }
    }

    /**
     * 获取功能名称
     */
    private String getFeatureName(String featureCode) {
        return switch (featureCode) {
            case "ai_parse" -> "AI简历解析";
            case "ai_match" -> "AI智能匹配";
            case "ai_generate_jd" -> "AI生成JD";
            case "talent_pool" -> "人才库";
            case "data_report" -> "数据报表";
            case "career_site" -> "招聘官网";
            case "api_access" -> "API对接";
            default -> featureCode;
        };
    }

    /**
     * 获取用量名称
     */
    private String getQuotaName(String quotaType) {
        return switch (quotaType) {
            case "job" -> "职位数量";
            case "resume" -> "简历数量";
            case "user" -> "用户数量";
            case "ai_parse" -> "本月AI解析次数";
            case "ai_match" -> "本月AI匹配次数";
            default -> quotaType;
        };
    }
}

