package ru.checkdev.notification.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.dto.CancelInterviewNotificationDTO;
import ru.checkdev.notification.service.InnerMessageService;
import ru.checkdev.notification.service.MessagesGenerator;
import ru.checkdev.notification.service.NotificationMessage;
import ru.checkdev.notification.service.UserTelegramService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Kafka-листенер события "отмена собеседования".
 * Заменяет POST /notification/cancelInterview/.
 */
@Slf4j
@Component
@AllArgsConstructor
public class InterviewCancelKafkaListener {

    private final UserTelegramService userTelegramService;
    private final InnerMessageService innerMessageService;
    private final NotificationMessage<UserTelegram, String, InnerMessage> notificationMessage;
    private final MessagesGenerator messagesGenerator;

    @KafkaListener(topics = KafkaTopics.INTERVIEW_CANCEL)
    public void onInterviewCancel(CancelInterviewNotificationDTO dto) {
        log.debug("Received interview cancel event: {}", dto);
        var message = messagesGenerator.getMessageCancelInterview(dto);
        InnerMessage innerMessage = InnerMessage.of()
                .userId(dto.getUserId())
                .text(message)
                .created(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .read(false)
                .interviewId(dto.getInterviewId())
                .build();
        innerMessageService.saveMessage(innerMessage);
        userTelegramService
                .findByUserId(dto.getUserId())
                .ifPresent(tg -> notificationMessage.sendMessage(tg, message));
    }
}