package com.freehire.modules.notification.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.common.base.BasePageQuery;
import com.freehire.common.response.PageResult;
import com.freehire.common.response.R;
import com.freehire.modules.notification.entity.Notification;
import com.freehire.modules.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 通知控制器
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Tag(name = "通知中心")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "分页查询通知")
    @GetMapping("/page")
    public R<PageResult<Notification>> getNotificationPage(BasePageQuery query) {
        Long userId = StpUtil.getLoginIdAsLong();
        IPage<Notification> page = notificationService.getNotificationPage(userId, query);
        return R.ok(PageResult.of(page));
    }

    @Operation(summary = "获取未读数量")
    @GetMapping("/unread-count")
    public R<Integer> getUnreadCount() {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(notificationService.getUnreadCount(userId));
    }

    @Operation(summary = "标记已读")
    @PostMapping("/{id}/read")
    public R<Void> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return R.ok();
    }

    @Operation(summary = "全部标记已读")
    @PostMapping("/read-all")
    public R<Void> markAllRead() {
        Long userId = StpUtil.getLoginIdAsLong();
        notificationService.markAllRead(userId);
        return R.ok();
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public R<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return R.ok();
    }
}

