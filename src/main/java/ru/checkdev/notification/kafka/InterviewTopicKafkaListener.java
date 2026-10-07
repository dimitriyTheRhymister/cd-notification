package ru.checkdev.notification.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.dto.InterviewNotifyDTO;
import ru.checkdev.notification.service.MessagesGenerator;
import ru.checkdev.notification.service.NotificationMessage;
import ru.checkdev.notification.service.UserTelegramService;

import java.util.List;

/**
 * Kafka-листенер события "новое сообщение по теме собеседования".
 * Заменяет POST /notification/topic/.
 */
@Slf4j
@Component
@AllArgsConstructor
public class InterviewTopicKafkaListener {

    private final UserTelegramService userTelegramService;
    private final NotificationMessage<UserTelegram, String, InnerMessage> notificationMessage;
    private final MessagesGenerator messagesGenerator;

    @KafkaListener(topics = KafkaTopics.INTERVIEW_TOPIC)
    public void onInterviewTopic(InterviewNotifyDTO dto) {
        log.debug("Received interview topic event: {}", dto);
        List<UserTelegram> usersTopic = userTelegramService
                .findAllByTopicIdAndUserIdNot(dto.getTopicId(), dto.getSubmitterId());
        var message = messagesGenerator.getMessageSubscribeTopic(dto);
        notificationMessage.sendMessage(usersTopic, message);
    }
}