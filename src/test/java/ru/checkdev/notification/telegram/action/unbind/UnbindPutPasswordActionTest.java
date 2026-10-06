package ru.checkdev.notification.telegram.action.unbind;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.checkdev.notification.telegram.SessionTg;

import static org.assertj.core.api.Assertions.assertThat;

class UnbindPutPasswordActionTest {
    private static final Chat CHAT = new Chat(1L, "type");
    private SessionTg sessionTg;
    private UnbindPutPasswordAction action;
    private Update update;

    @BeforeEach
    void setUp() {
        sessionTg = new SessionTg();
        action = new UnbindPutPasswordAction(sessionTg);
        update = new Update();
        Message message = new Message();
        message.setChat(CHAT);
        message.setText("password123");
        update.setMessage(message);
    }

    @Test
    void whenHandleThenSessionStoresPassword() {
        action.handle(update);
        String actual = sessionTg.get(String.valueOf(CHAT.getId()), "password", "");
        assertThat(actual).isEqualTo("password123");
    }
}