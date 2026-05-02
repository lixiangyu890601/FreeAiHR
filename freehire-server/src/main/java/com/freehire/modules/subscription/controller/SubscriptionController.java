package com.freehire.modules.subscription.controller;

import com.freehire.common.response.R;
import com.freehire.modules.subscription.dto.SubscriptionInfo;
import com.freehire.modules.subscription.entity.SubscriptionPlan;
import com.freehire.modules.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订阅管理控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "订阅管理")
@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "获取当前订阅信息")
    @GetMapping("/current")
    public R<SubscriptionInfo> getCurrentSubscription() {
        return R.ok(subscriptionService.getCurrentSubscription());
    }

    @Operation(summary = "获取所有套餐列表")
    @GetMapping("/plans")
    public R<List<SubscriptionPlan>> getAllPlans() {
        return R.ok(subscriptionService.getAllPlans());
    }

    @Operation(summary = "激活套餐")
    @PostMapping("/activate")
    public R<Void> activatePlan(@RequestParam String planCode, 
                                @RequestParam(required = false) String licenseKey) {
        subscriptionService.activatePlan(planCode, licenseKey);
        return R.ok();
    }
}

