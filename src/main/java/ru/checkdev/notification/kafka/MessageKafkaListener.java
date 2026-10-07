package ru.checkdev.notification.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.service.InnerMessageService;

/**
 * Kafka-листенер события "новое входящее сообщение для пользователя".
 * Заменяет POST /messages/message.
 */
@Slf4j
@Component
@AllArgsConstructor
public class MessageKafkaListener {

    private final InnerMessageService messageService;

    @KafkaListener(topics = KafkaTopics.MESSAGE)
    public void onMessage(InnerMessage innerMessage) {
        log.debug("Received inner message event: {}", innerMessage);
        messageService.send(innerMessage);
    }
}