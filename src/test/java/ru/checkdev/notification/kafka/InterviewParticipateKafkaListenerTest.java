package ru.checkdev.notification.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.checkdev.notification.domain.InnerMessage;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.dto.WisherNotifyDTO;
import ru.checkdev.notification.service.InnerMessageService;
import ru.checkdev.notification.service.MessagesGenerator;
import ru.checkdev.notification.service.NotificationMessage;
import ru.checkdev.notification.service.UserTelegramService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InterviewParticipateKafkaListenerTest {

    @Mock
    private UserTelegramService userTelegramService;
    @Mock
    private InnerMessageService innerMessageService;
    @Mock
    private NotificationMessage<UserTelegram, String, InnerMessage> notificationMessage;
    @Mock
    private MessagesGenerator messagesGenerator;

    private InterviewParticipateKafkaListener listener;

    @BeforeEach
    void setUp() {
        listener = new InterviewParticipateKafkaListener(
                userTelegramService, innerMessageService, notificationMessage, messagesGenerator);
    }

    @Test
    void whenOnInterviewParticipateThenSaveAndSend() {
        WisherNotifyDTO dto = new WisherNotifyDTO();
        dto.setInterviewId(7);
        dto.setSubmitterId(11);
        String generatedMessage = "participate message";
        UserTelegram tg = new UserTelegram(0, 11, 99L, false);

        Mockito.when(messagesGenerator.getMessageParticipateWisher(dto))
                .thenReturn(generatedMessage);
        Mockito.when(userTelegramService.findByUserId(11))
                .thenReturn(Optional.of(tg));

        listener.onInterviewParticipate(dto);

        ArgumentCaptor<InnerMessage> captor = ArgumentCaptor.forClass(InnerMessage.class);
        Mockito.verify(innerMessageService, Mockito.times(1)).saveMessage(captor.capture());
        InnerMessage saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(11);
        assertThat(saved.getText()).isEqualTo(generatedMessage);
        assertThat(saved.getInterviewId()).isEqualTo(7);
        assertThat(saved.isRead()).isFalse();

        Mockito.verify(notificationMessage, Mockito.times(1))
                .sendMessage(tg, generatedMessage);
    }

    @Test
    void whenOnInterviewParticipateAndNoTelegramThenOnlySave() {
        WisherNotifyDTO dto = new WisherNotifyDTO();
        dto.setInterviewId(7);
        dto.setSubmitterId(11);
        String generatedMessage = "participate message";

        Mockito.when(messagesGenerator.getMessageParticipateWisher(dto))
                .thenReturn(generatedMessage);
        Mockito.when(userTelegramService.findByUserId(11))
                .thenReturn(Optional.empty());

        listener.onInterviewParticipate(dto);

        Mockito.verify(innerMessageService, Mockito.times(1))
                .saveMessage(Mockito.any(InnerMessage.class));
        Mockito.verify(notificationMessage, Mockito.never())
                .sendMessage(Mockito.any(UserTelegram.class), Mockito.anyString());
    }
}