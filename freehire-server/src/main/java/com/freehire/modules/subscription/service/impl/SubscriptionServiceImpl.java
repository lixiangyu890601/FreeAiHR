package com.freehire.modules.subscription.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freehire.common.exception.BusinessException;
import com.freehire.modules.subscription.dto.SubscriptionInfo;
import com.freehire.modules.subscription.entity.SubscriptionPlan;
import com.freehire.modules.subscription.entity.SubscriptionStatus;
import com.freehire.modules.subscription.entity.UsageStats;
import com.freehire.modules.subscription.mapper.SubscriptionPlanMapper;
import com.freehire.modules.subscription.mapper.SubscriptionStatusMapper;
import com.freehire.modules.subscription.mapper.UsageStatsMapper;
import com.freehire.modules.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 订阅服务实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionPlanMapper planMapper;
    private final SubscriptionStatusMapper statusMapper;
    private final UsageStatsMapper statsMapper;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    @Cacheable(value = "subscription", key = "'current'")
    public SubscriptionInfo getCurrentSubscription() {
        // 获取订阅状态
        SubscriptionStatus status = statusMapper.selectById(1L);
        if (status == null) {
            throw new BusinessException("系统未初始化订阅状态");
        }

        // 获取套餐信息
        SubscriptionPlan plan = planMapper.selectById(status.getPlanId());
        if (plan == null) {
            throw new BusinessException("套餐不存在");
        }

        // 获取当月用量
        String currentMonth = LocalDate.now().format(MONTH_FORMATTER);
        UsageStats stats = statsMapper.selectOne(new LambdaQueryWrapper<UsageStats>()
                .eq(UsageStats::getStatMonth, currentMonth));

        // 构建订阅信息
        SubscriptionInfo info = new SubscriptionInfo();
        
        // 套餐基本信息
        info.setPlanCode(plan.getPlanCode());
        info.setPlanName(plan.getPlanName());
        info.setDescription(plan.getDescription());
        info.setPrice(plan.getPrice());
        info.setBillingCycle(plan.getBillingCycle());

        // 功能权限
        info.setFeatureAiParse(plan.getFeatureAiParse() == 1);
        info.setFeatureAiMatch(plan.getFeatureAiMatch() == 1);
        info.setFeatureAiGenerateJd(plan.getFeatureAiGenerateJd() == 1);
        info.setFeatureTalentPool(plan.getFeatureTalentPool() == 1);
        info.setFeatureDataReport(plan.getFeatureDataReport() == 1);
        info.setFeatureCareerSite(plan.getFeatureCareerSite() == 1);
        info.setFeatureApiAccess(plan.getFeatureApiAccess() == 1);

        // 用量限制
        info.setLimitJobCount(plan.getLimitJobCount());
        info.setLimitResumeCount(plan.getLimitResumeCount());
        info.setLimitUserCount(plan.getLimitUserCount());
        info.setLimitAiParseMonthly(plan.getLimitAiParseMonthly());
        info.setLimitAiMatchMonthly(plan.getLimitAiMatchMonthly());

        // 当前用量
        if (stats != null) {
            info.setCurrentJobCount(stats.getJobCount());
            info.setCurrentResumeCount(stats.getResumeCount());
            info.setCurrentUserCount(stats.getUserCount());
            info.setCurrentAiParseCount(stats.getAiParseCount());
            info.setCurrentAiMatchCount(stats.getAiMatchCount());
        } else {
            info.setCurrentJobCount(0);
            info.setCurrentResumeCount(0);
            info.setCurrentUserCount(0);
            info.setCurrentAiParseCount(0);
            info.setCurrentAiMatchCount(0);
        }

        // 订阅状态
        info.setSubscriptionStatus(status.getStatus());
        info.setExpiresAt(status.getExpiresAt());

        return info;
    }

    @Override
    public SubscriptionPlan getCurrentPlan() {
        SubscriptionStatus status = statusMapper.selectById(1L);
        if (status == null) {
            return null;
        }
        return planMapper.selectById(status.getPlanId());
    }

    @Override
    public List<SubscriptionPlan> getAllPlans() {
        return planMapper.selectList(new LambdaQueryWrapper<SubscriptionPlan>()
                .eq(SubscriptionPlan::getStatus, 1)
                .orderByAsc(SubscriptionPlan::getSort));
    }

    @Override
    public boolean hasFeature(String featureCode) {
        SubscriptionInfo info = getCurrentSubscription();
        return info.hasFeature(featureCode);
    }

    @Override
    public boolean checkQuota(String quotaType) {
        SubscriptionInfo info = getCurrentSubscription();
        return info.checkQuota(quotaType);
    }

    @Override
    @Transactional
    @CacheEvict(value = "subscription", key = "'current'")
    public void activatePlan(String planCode, String licenseKey) {
        // 查找套餐
        SubscriptionPlan plan = planMapper.selectOne(new LambdaQueryWrapper<SubscriptionPlan>()
                .eq(SubscriptionPlan::getPlanCode, planCode));
        if (plan == null) {
            throw new BusinessException("套餐不存在: " + planCode);
        }

        // 更新订阅状态
        SubscriptionStatus status = statusMapper.selectById(1L);
        if (status == null) {
            status = new SubscriptionStatus();
            status.setId(1L);
        }

        status.setPlanId(plan.getId());
        status.setLicenseKey(licenseKey);
        status.setActivatedAt(LocalDateTime.now());
        status.setStatus(1);

        // 设置过期时间
        if ("monthly".equals(plan.getBillingCycle())) {
            status.setExpiresAt(LocalDateTime.now().plusMonths(1));
        } else if ("yearly".equals(plan.getBillingCycle())) {
            status.setExpiresAt(LocalDateTime.now().plusYears(1));
        } else {
            status.setExpiresAt(null); // 永久
        }

        if (status.getId() != null && statusMapper.selectById(1L) != null) {
            statusMapper.updateById(status);
        } else {
            statusMapper.insert(status);
        }

        log.info("激活套餐成功: {}", planCode);
    }

    @Override
    @CacheEvict(value = "subscription", key = "'current'")
    public void incrementUsage(String usageType, int delta) {
        String currentMonth = LocalDate.now().format(MONTH_FORMATTER);
        ensureCurrentMonthStats();

        switch (usageType) {
            case "job" -> statsMapper.incrementJobCount(currentMonth, delta);
            case "resume" -> statsMapper.incrementResumeCount(currentMonth, delta);
            case "candidate" -> statsMapper.incrementCandidateCount(currentMonth, delta);
            case "ai_parse" -> statsMapper.incrementAiParseCount(currentMonth, delta);
            case "ai_match" -> statsMapper.incrementAiMatchCount(currentMonth, delta);
            default -> log.warn("未知的用量类型: {}", usageType);
        }
    }

    @Override
    public void ensureCurrentMonthStats() {
        String currentMonth = LocalDate.now().format(MONTH_FORMATTER);
        UsageStats stats = statsMapper.selectOne(new LambdaQueryWrapper<UsageStats>()
                .eq(UsageStats::getStatMonth, currentMonth));
        
        if (stats == null) {
            stats = new UsageStats();
            stats.setStatMonth(currentMonth);
            stats.setJobCount(0);
            stats.setResumeCount(0);
            stats.setCandidateCount(0);
            stats.setUserCount(0);
            stats.setAiParseCount(0);
            stats.setAiMatchCount(0);
            stats.setAiGenerateCount(0);
            statsMapper.insert(stats);
        }
    }
}

