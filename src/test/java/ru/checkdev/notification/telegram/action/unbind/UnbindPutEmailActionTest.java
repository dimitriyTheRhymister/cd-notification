package ru.checkdev.notification.telegram.action.unbind;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.checkdev.notification.telegram.SessionTg;

import static org.assertj.core.api.Assertions.assertThat;

class UnbindPutEmailActionTest {
    private static final Chat CHAT = new Chat(1L, "type");
    private SessionTg sessionTg;
    private UnbindPutEmailAction action;
    private Update update;

    @BeforeEach
    void setUp() {
        sessionTg = new SessionTg();
        action = new UnbindPutEmailAction(sessionTg);
        update = new Update();
        Message message = new Message();
        message.setChat(CHAT);
        message.setText("user@example.com");
        update.setMessage(message);
    }

    @Test
    void whenHandleThenSessionStoresEmail() {
        action.handle(update);
        String actual = sessionTg.get(String.valueOf(CHAT.getId()), "email", "");
        assertThat(actual).isEqualTo("user@example.com");
    }
}