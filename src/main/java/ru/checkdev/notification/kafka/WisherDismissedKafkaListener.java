package ru.checkdev.notification.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.dto.WisherDismissedDTO;
import ru.checkdev.notification.service.InnerMessageService;
import ru.checkdev.notification.service.MessagesGenerator;
import ru.checkdev.notification.service.NotificationMessage;
import ru.checkdev.notification.service.UserTelegramService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Kafka-листенер события "участник отстранён от собеседования".
 * Заменяет POST /notification/participantIsDismissed/.
 * Одно сообщение — один dismissed-участник.
 */
@Slf4j
@Component
@AllArgsConstructor
public class WisherDismissedKafkaListener {

    private final UserTelegramService userTelegramService;
    private final InnerMessageService innerMessageService;
    private final NotificationMessage<UserTelegram, String, InnerMessage> notificationMessage;
    private final MessagesGenerator messagesGenerator;

    @KafkaListener(topics = KafkaTopics.WISHER_DISMISSED)
    public void onWisherDismissed(WisherDismissedDTO wisher) {
        log.debug("Received wisher dismissed event: {}", wisher);
        var message = messagesGenerator.getMessageDismissedWisher(wisher);
        InnerMessage innerMessage = InnerMessage.of()
                .userId(wisher.getUserId())
                .text(message)
                .created(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)))
                .read(false)
                .interviewId(wisher.getInterviewId())
                .build();
        innerMessageService.saveMessage(innerMessage);
        userTelegramService
                .findByUserId(wisher.getUserId())
                .ifPresent(tg -> notificationMessage.sendMessage(tg, message));
    }
}