package ru.checkdev.notification.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.service.InnerMessageService;

@ExtendWith(MockitoExtension.class)
class MessageKafkaListenerTest {

    @Mock
    private InnerMessageService messageService;

    private MessageKafkaListener listener;

    @BeforeEach
    void setUp() {
        listener = new MessageKafkaListener(messageService);
    }

    @Test
    void whenOnMessageThenCallServiceSend() {
        InnerMessage innerMessage = new InnerMessage(1, 10, "text", null, false);

        listener.onMessage(innerMessage);

        Mockito.verify(messageService, Mockito.times(1))
                .send(innerMessage);
    }
}