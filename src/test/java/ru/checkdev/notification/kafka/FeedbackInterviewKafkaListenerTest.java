package ru.checkdev.notification.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.checkdev.notification.dto.FeedbackNotificationDTO;
import ru.checkdev.notification.service.NotificationMessagesService;

@ExtendWith(MockitoExtension.class)
class FeedbackInterviewKafkaListenerTest {

    @Mock
    private NotificationMessagesService notificationMessagesService;

    private FeedbackInterviewKafkaListener listener;

    @BeforeEach
    void setUp() {
        listener = new FeedbackInterviewKafkaListener(notificationMessagesService);
    }

    @Test
    void whenOnFeedbackInterviewThenCallService() {
        FeedbackNotificationDTO dto = new FeedbackNotificationDTO();

        listener.onFeedbackInterview(dto);

        Mockito.verify(notificationMessagesService, Mockito.times(1))
                .sendFeedbackNotification(dto);
    }
}