package ru.checkdev.notification.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.dto.WisherNotifyDTO;
import ru.checkdev.notification.service.InnerMessageService;
import ru.checkdev.notification.service.MessagesGenerator;
import ru.checkdev.notification.service.NotificationMessage;
import ru.checkdev.notification.service.UserTelegramService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Kafka-листенер события "новый участник собеседования".
 * Заменяет POST /notification/participate/.
 */
@Slf4j
@Component
@AllArgsConstructor
public class InterviewParticipateKafkaListener {

    private final UserTelegramService userTelegramService;
    private final InnerMessageService innerMessageService;
    private final NotificationMessage<UserTelegram, String, InnerMessage> notificationMessage;
    private final MessagesGenerator messagesGenerator;

    @KafkaListener(topics = KafkaTopics.INTERVIEW_PARTICIPATE)
    public void onInterviewParticipate(WisherNotifyDTO dto) {
        log.debug("Received interview participate event: {}", dto);
        var message = messagesGenerator.getMessageParticipateWisher(dto);
        InnerMessage innerMessage = InnerMessage.of()
                .userId(dto.getSubmitterId())
                .text(message)
                .created(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .read(false)
                .interviewId(dto.getInterviewId())
                .build();
        innerMessageService.saveMessage(innerMessage);
        userTelegramService
                .findByUserId(dto.getSubmitterId())
                .ifPresent(tg -> notificationMessage.sendMessage(tg, message));
    }
}