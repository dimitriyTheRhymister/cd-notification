package ru.checkdev.notification.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.checkdev.notification.dto.CategoryWithTopicDTO;
import ru.checkdev.notification.service.InnerMessageService;
import ru.checkdev.notification.service.NotificationMessagesService;
import ru.checkdev.notification.service.SubscribeCategoryService;
import ru.checkdev.notification.service.SubscribeTopicService;

import java.util.List;

/**
 * Kafka-листенер события "создано новое собеседование".
 * Заменяет POST /messages/newInterview.
 */
@Slf4j
@Component
@AllArgsConstructor
public class NewInterviewKafkaListener {

    private final InnerMessageService messageService;
    private final SubscribeCategoryService categoryService;
    private final SubscribeTopicService topicService;
    private final NotificationMessagesService notificationMessagesService;

    @Transactional
    @KafkaListener(topics = KafkaTopics.NEW_INTERVIEW)
    public void onNewInterview(CategoryWithTopicDTO dto) {
        log.debug("Received new interview event: {}", dto);
        List<Integer> categorySubscribersIds = categoryService.findUserIdsByCategoryIdExcludeCurrent(
                dto.getCategoryId(), dto.getSubmitterId());
        List<Integer> topicSubscribersIds = topicService.findUserIdsByTopicIdExcludeCurrent(
                dto.getTopicId(), dto.getSubmitterId());
        messageService.saveMessagesForSubscribers(dto, categorySubscribersIds, topicSubscribersIds);
        notificationMessagesService.sendMessagesToCategorySubscribers(categorySubscribersIds, dto);
    }
}