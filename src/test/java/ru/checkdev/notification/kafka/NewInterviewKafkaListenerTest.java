package ru.checkdev.notification.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.checkdev.notification.dto.CategoryWithTopicDTO;
import ru.checkdev.notification.service.InnerMessageService;
import ru.checkdev.notification.service.NotificationMessagesService;
import ru.checkdev.notification.service.SubscribeCategoryService;
import ru.checkdev.notification.service.SubscribeTopicService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class NewInterviewKafkaListenerTest {

    @Mock
    private InnerMessageService messageService;
    @Mock
    private SubscribeCategoryService categoryService;
    @Mock
    private SubscribeTopicService topicService;
    @Mock
    private NotificationMessagesService notificationMessagesService;

    private NewInterviewKafkaListener listener;

    @BeforeEach
    void setUp() {
        listener = new NewInterviewKafkaListener(
                messageService, categoryService, topicService, notificationMessagesService);
    }

    @Test
    void whenOnNewInterviewThenSaveAndNotifySubscribers() {
        CategoryWithTopicDTO dto = new CategoryWithTopicDTO();
        dto.setCategoryId(1);
        dto.setTopicId(2);
        dto.setSubmitterId(3);
        List<Integer> categorySubscribers = List.of(10, 20);
        List<Integer> topicSubscribers = List.of(30);

        Mockito.when(categoryService.findUserIdsByCategoryIdExcludeCurrent(1, 3))
                .thenReturn(categorySubscribers);
        Mockito.when(topicService.findUserIdsByTopicIdExcludeCurrent(2, 3))
                .thenReturn(topicSubscribers);

        listener.onNewInterview(dto);

        Mockito.verify(categoryService, Mockito.times(1))
                .findUserIdsByCategoryIdExcludeCurrent(1, 3);
        Mockito.verify(topicService, Mockito.times(1))
                .findUserIdsByTopicIdExcludeCurrent(2, 3);
        Mockito.verify(messageService, Mockito.times(1))
                .saveMessagesForSubscribers(dto, categorySubscribers, topicSubscribers);
        Mockito.verify(notificationMessagesService, Mockito.times(1))
                .sendMessagesToCategorySubscribers(categorySubscribers, dto);
    }
}