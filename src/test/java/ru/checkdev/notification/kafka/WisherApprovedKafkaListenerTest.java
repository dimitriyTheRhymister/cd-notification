package ru.checkdev.notification.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.checkdev.notification.dto.WisherApprovedDTO;
import ru.checkdev.notification.service.NotificationMessagesService;

@ExtendWith(MockitoExtension.class)
class WisherApprovedKafkaListenerTest {

    @Mock
    private NotificationMessagesService notificationMessagesService;

    private WisherApprovedKafkaListener listener;

    @BeforeEach
    void setUp() {
        listener = new WisherApprovedKafkaListener(notificationMessagesService);
    }

    @Test
    void whenOnWisherApprovedThenCallService() {
        WisherApprovedDTO dto = new WisherApprovedDTO();

        listener.onWisherApproved(dto);

        Mockito.verify(notificationMessagesService, Mockito.times(1))
                .sendApprovedNotification(dto);
    }
}