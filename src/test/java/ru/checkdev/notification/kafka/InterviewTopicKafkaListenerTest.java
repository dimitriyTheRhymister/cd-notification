package ru.checkdev.notification.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.dto.InterviewNotifyDTO;
import ru.checkdev.notification.service.MessagesGenerator;
import ru.checkdev.notification.service.NotificationMessage;
import ru.checkdev.notification.service.UserTelegramService;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class InterviewTopicKafkaListenerTest {

    @Mock
    private UserTelegramService userTelegramService;
    @Mock
    private NotificationMessage<UserTelegram, String, InnerMessage> notificationMessage;
    @Mock
    private MessagesGenerator messagesGenerator;

    private InterviewTopicKafkaListener listener;

    @BeforeEach
    void setUp() {
        listener = new InterviewTopicKafkaListener(
                userTelegramService, notificationMessage, messagesGenerator);
    }

    @Test
    void whenOnInterviewTopicThenSendToSubscribers() {
        InterviewNotifyDTO dto = new InterviewNotifyDTO();
        dto.setTopicId(5);
        dto.setSubmitterId(10);
        List<UserTelegram> subscribers = Collections.singletonList(
                new UserTelegram(0, 3, 42L, false));
        String generatedMessage = "topic message";

        Mockito.when(userTelegramService.findAllByTopicIdAndUserIdNot(5, 10))
                .thenReturn(subscribers);
        Mockito.when(messagesGenerator.getMessageSubscribeTopic(dto))
                .thenReturn(generatedMessage);

        listener.onInterviewTopic(dto);

        Mockito.verify(userTelegramService, Mockito.times(1))
                .findAllByTopicIdAndUserIdNot(5, 10);
        Mockito.verify(messagesGenerator, Mockito.times(1))
                .getMessageSubscribeTopic(dto);
        Mockito.verify(notificationMessage, Mockito.times(1))
                .sendMessage(subscribers, generatedMessage);
    }

    @Test
    void whenOnInterviewTopicAndNoSubscribersThenStillCallSendMessage() {
        InterviewNotifyDTO dto = new InterviewNotifyDTO();
        dto.setTopicId(5);
        dto.setSubmitterId(10);
        String generatedMessage = "topic message";

        Mockito.when(userTelegramService.findAllByTopicIdAndUserIdNot(5, 10))
                .thenReturn(Collections.emptyList());
        Mockito.when(messagesGenerator.getMessageSubscribeTopic(dto))
                .thenReturn(generatedMessage);

        listener.onInterviewTopic(dto);

        Mockito.verify(notificationMessage, Mockito.times(1))
                .sendMessage(Collections.emptyList(), generatedMessage);
    }
}