package com.freehire.common.manager;

import com.freehire.common.exception.BusinessException;
import com.freehire.common.response.ResultCode;
import com.freehire.modules.subscription.dto.SubscriptionInfo;
import com.freehire.modules.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 配额管理器
 * 统一处理功能权限和用量检查
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuotaManager {

    private final SubscriptionService subscriptionService;

    /**
     * 检查功能权限
     *
     * @param featureCode 功能代码
     * @throws BusinessException 如果没有权限
     */
    public void requireFeature(String featureCode) {
        if (!hasFeature(featureCode)) {
            log.warn("功能权限不足: {}", featureCode);
            throw new BusinessException(ResultCode.FEATURE_NOT_AVAILABLE.getCode(),
                    "当前套餐不支持此功能，请升级套餐");
        }
    }

    /**
     * 检查功能权限
     *
     * @param featureCode 功能代码
     * @param message     自定义错误消息
     * @throws BusinessException 如果没有权限
     */
    public void requireFeature(String featureCode, String message) {
        if (!hasFeature(featureCode)) {
            log.warn("功能权限不足: {}", featureCode);
            throw new BusinessException(ResultCode.FEATURE_NOT_AVAILABLE.getCode(), message);
        }
    }

    /**
     * 检查用量配额
     *
     * @param quotaType 配额类型
     * @throws BusinessException 如果超出配额
     */
    public void requireQuota(String quotaType) {
        if (!checkQuota(quotaType)) {
            log.warn("配额不足: {}", quotaType);
            throw new BusinessException(ResultCode.QUOTA_EXCEEDED.getCode(),
                    getQuotaExceededMessage(quotaType));
        }
    }

    /**
     * 检查用量配额
     *
     * @param quotaType 配额类型
     * @param message   自定义错误消息
     * @throws BusinessException 如果超出配额
     */
    public void requireQuota(String quotaType, String message) {
        if (!checkQuota(quotaType)) {
            log.warn("配额不足: {}", quotaType);
            throw new BusinessException(ResultCode.QUOTA_EXCEEDED.getCode(), message);
        }
    }

    /**
     * 检查是否有功能权限
     *
     * @param featureCode 功能代码
     * @return 是否有权限
     */
    public boolean hasFeature(String featureCode) {
        try {
            return subscriptionService.hasFeature(featureCode);
        } catch (Exception e) {
            log.error("检查功能权限失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查配额是否足够
     *
     * @param quotaType 配额类型
     * @return 是否有足够配额
     */
    public boolean checkQuota(String quotaType) {
        try {
            return subscriptionService.checkQuota(quotaType);
        } catch (Exception e) {
            log.error("检查配额失败: {}", e.getMessage());
            return true; // 检查失败时默认允许，避免影响正常使用
        }
    }

    /**
     * 增加用量
     *
     * @param usageType 用量类型
     * @param delta     增量
     */
    public void incrementUsage(String usageType, int delta) {
        try {
            subscriptionService.incrementUsage(usageType, delta);
        } catch (Exception e) {
            log.error("更新用量统计失败: {}", e.getMessage());
        }
    }

    /**
     * 获取当前订阅信息
     *
     * @return 订阅信息
     */
    public SubscriptionInfo getCurrentSubscription() {
        return subscriptionService.getCurrentSubscription();
    }

    /**
     * 获取配额超限消息
     */
    private String getQuotaExceededMessage(String quotaType) {
        return switch (quotaType) {
            case "job" -> "职位数量已达上限，请升级套餐";
            case "resume" -> "简历数量已达上限，请升级套餐";
            case "candidate" -> "候选人数量已达上限，请升级套餐";
            case "ai_parse" -> "本月AI解析次数已达上限，请升级套餐";
            case "ai_match" -> "本月AI匹配次数已达上限，请升级套餐";
            default -> "用量已达上限，请升级套餐";
        };
    }
}

