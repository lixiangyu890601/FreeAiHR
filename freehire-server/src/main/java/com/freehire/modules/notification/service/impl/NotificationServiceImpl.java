package com.freehire.modules.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freehire.common.base.BasePageQuery;
import com.freehire.common.exception.BusinessException;
import com.freehire.modules.notification.entity.Notification;
import com.freehire.modules.notification.mapper.NotificationMapper;
import com.freehire.modules.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 通知服务实现
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public IPage<Notification> getNotificationPage(Long userId, BasePageQuery query) {
        Page<Notification> page = new Page<>(query.getCurrent(), query.getSize());
        return notificationMapper.selectPage(page, new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreateTime));
    }

    @Override
    public int getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }

    @Override
    public void markRead(Long id) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }
        if (notification.getIsRead() == 0) {
            notification.setIsRead(1);
            notification.setReadTime(LocalDateTime.now());
            notificationMapper.updateById(notification);
        }
    }

    @Override
    public void markAllRead(Long userId) {
        notificationMapper.markAllRead(userId);
    }

    @Override
    public void deleteNotification(Long id) {
        notificationMapper.deleteById(id);
    }

    @Override
    public void send(Long userId, String type, String title, String content) {
        send(userId, type, title, content, null, null, null);
    }

    @Override
    public void send(Long userId, String type, String title, String content, Long bizId, String bizType, String link) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setBizId(bizId);
        notification.setBizType(bizType);
        notification.setLink(link);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
        log.info("发送通知: userId={}, title={}", userId, title);
    }

    @Override
    public void sendNewResumeNotification(Long userId, String candidateName, String jobTitle, Long resumeId) {
        send(userId, "resume",
                "新简历投递",
                String.format("候选人 %s 投递了职位：%s", candidateName, jobTitle),
                resumeId, "resume", "/recruit/resume");
    }

    @Override
    public void sendInterviewReminder(Long userId, String candidateName, String interviewTime, Long interviewId) {
        send(userId, "interview",
                "面试提醒",
                String.format("您有一场面试即将开始：%s - %s", candidateName, interviewTime),
                interviewId, "interview", "/recruit/interview");
    }

    @Override
    public void sendStageChangeNotification(Long userId, String candidateName, String newStage, Long applicationId) {
        send(userId, "application",
                "候选人状态变更",
                String.format("候选人 %s 已进入阶段：%s", candidateName, newStage),
                applicationId, "application", "/recruit/candidate");
    }
}

