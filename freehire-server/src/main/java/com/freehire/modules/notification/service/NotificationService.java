package com.freehire.modules.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.freehire.common.base.BasePageQuery;
import com.freehire.modules.notification.entity.Notification;

/**
 * 通知服务接口
 *
 * @author FreeHire
 * @since 1.0.0
 */
public interface NotificationService {

    /**
     * 分页查询用户通知
     */
    IPage<Notification> getNotificationPage(Long userId, BasePageQuery query);

    /**
     * 获取未读数量
     */
    int getUnreadCount(Long userId);

    /**
     * 标记已读
     */
    void markRead(Long id);

    /**
     * 全部标记已读
     */
    void markAllRead(Long userId);

    /**
     * 删除通知
     */
    void deleteNotification(Long id);

    /**
     * 发送通知
     */
    void send(Long userId, String type, String title, String content);

    /**
     * 发送通知（带业务关联）
     */
    void send(Long userId, String type, String title, String content, Long bizId, String bizType, String link);

    /**
     * 发送新简历通知
     */
    void sendNewResumeNotification(Long userId, String candidateName, String jobTitle, Long resumeId);

    /**
     * 发送面试提醒
     */
    void sendInterviewReminder(Long userId, String candidateName, String interviewTime, Long interviewId);

    /**
     * 发送阶段变更通知
     */
    void sendStageChangeNotification(Long userId, String candidateName, String newStage, Long applicationId);
}

