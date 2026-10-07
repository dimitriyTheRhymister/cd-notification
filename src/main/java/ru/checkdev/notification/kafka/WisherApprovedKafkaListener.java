package ru.checkdev.notification.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.checkdev.notification.dto.WisherApprovedDTO;
import ru.checkdev.notification.service.NotificationMessagesService;

/**
 * Kafka-листенер события одобрения участника собеседования.
 * Заменяет POST /notificationWisher/approvedWisher/.
 */
@Slf4j
@Component
@AllArgsConstructor
public class WisherApprovedKafkaListener {

    private final NotificationMessagesService notificationMessagesService;

    @KafkaListener(topics = KafkaTopics.WISHER_APPROVED)
    public void onWisherApproved(WisherApprovedDTO dto) {
        log.debug("Received wisher approved event: {}", dto);
        notificationMessagesService.sendApprovedNotification(dto);
    }
}