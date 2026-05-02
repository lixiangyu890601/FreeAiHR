package com.freehire.modules.subscription.service;

import com.freehire.modules.subscription.dto.SubscriptionInfo;
import com.freehire.modules.subscription.entity.SubscriptionPlan;

import java.util.List;

/**
 * 订阅服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface SubscriptionService {

    /**
     * 获取当前订阅信息（包含套餐和用量）
     */
    SubscriptionInfo getCurrentSubscription();

    /**
     * 获取当前套餐
     */
    SubscriptionPlan getCurrentPlan();

    /**
     * 获取所有套餐列表
     */
    List<SubscriptionPlan> getAllPlans();

    /**
     * 检查功能是否可用
     */
    boolean hasFeature(String featureCode);

    /**
     * 检查用量是否超限
     * @return true表示未超限可用，false表示已超限
     */
    boolean checkQuota(String quotaType);

    /**
     * 激活套餐
     */
    void activatePlan(String planCode, String licenseKey);

    /**
     * 增加用量计数
     */
    void incrementUsage(String usageType, int delta);

    /**
     * 获取当前月份的用量统计
     */
    void ensureCurrentMonthStats();
}

