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
import ru.checkdev.notification.dto.CancelInterviewNotificationDTO;
import ru.checkdev.notification.service.InnerMessageService;
import ru.checkdev.notification.service.MessagesGenerator;
import ru.checkdev.notification.service.NotificationMessage;
import ru.checkdev.notification.service.UserTelegramService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InterviewCancelKafkaListenerTest {

    @Mock
    private UserTelegramService userTelegramService;
    @Mock
    private InnerMessageService innerMessageService;
    @Mock
    private NotificationMessage<UserTelegram, String, InnerMessage> notificationMessage;
    @Mock
    private MessagesGenerator messagesGenerator;

    private InterviewCancelKafkaListener listener;

    @BeforeEach
    void setUp() {
        listener = new InterviewCancelKafkaListener(
                userTelegramService, innerMessageService, notificationMessage, messagesGenerator);
    }

    @Test
    void whenOnInterviewCancelThenSaveAndSend() {
        CancelInterviewNotificationDTO dto = new CancelInterviewNotificationDTO();
        dto.setInterviewId(3);
        dto.setUserId(8);
        String generatedMessage = "cancel message";
        UserTelegram tg = new UserTelegram(0, 8, 77L, false);

        Mockito.when(messagesGenerator.getMessageCancelInterview(dto))
                .thenReturn(generatedMessage);
        Mockito.when(userTelegramService.findByUserId(8))
                .thenReturn(Optional.of(tg));

        listener.onInterviewCancel(dto);

        ArgumentCaptor<InnerMessage> captor = ArgumentCaptor.forClass(InnerMessage.class);
        Mockito.verify(innerMessageService, Mockito.times(1)).saveMessage(captor.capture());
        InnerMessage saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(8);
        assertThat(saved.getText()).isEqualTo(generatedMessage);
        assertThat(saved.getInterviewId()).isEqualTo(3);
        assertThat(saved.isRead()).isFalse();

        Mockito.verify(notificationMessage, Mockito.times(1))
                .sendMessage(tg, generatedMessage);
    }

    @Test
    void whenOnInterviewCancelAndNoTelegramThenOnlySave() {
        CancelInterviewNotificationDTO dto = new CancelInterviewNotificationDTO();
        dto.setInterviewId(3);
        dto.setUserId(8);
        String generatedMessage = "cancel message";

        Mockito.when(messagesGenerator.getMessageCancelInterview(dto))
                .thenReturn(generatedMessage);
        Mockito.when(userTelegramService.findByUserId(8))
                .thenReturn(Optional.empty());

        listener.onInterviewCancel(dto);

        Mockito.verify(innerMessageService, Mockito.times(1))
                .saveMessage(Mockito.any(InnerMessage.class));
        Mockito.verify(notificationMessage, Mockito.never())
                .sendMessage(Mockito.any(UserTelegram.class), Mockito.anyString());
    }
}