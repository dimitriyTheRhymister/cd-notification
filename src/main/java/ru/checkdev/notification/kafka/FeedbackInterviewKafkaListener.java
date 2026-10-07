package ru.checkdev.notification.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.checkdev.notification.dto.FeedbackNotificationDTO;
import ru.checkdev.notification.service.NotificationMessagesService;

/**
 * Kafka-листенер событий обратной связи по собеседованиям.
 * Заменяет POST /feedback/interview.
 */
@Slf4j
@Component
@AllArgsConstructor
public class FeedbackInterviewKafkaListener {

    private final NotificationMessagesService notificationMessagesService;

    @KafkaListener(
            topics = KafkaTopics.FEEDBACK_INTERVIEW,
            groupId = "notification-group"
    )
    public void onFeedbackInterview(FeedbackNotificationDTO dto) {
        log.debug("Received feedback interview event: {}", dto);
        notificationMessagesService.sendFeedbackNotification(dto);
    }
}