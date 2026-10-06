package ru.checkdev.notification.telegram.action.unbind;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.assertj.core.api.Assertions.assertThat;

class UnbindAskPasswordActionTest {
    private static final Chat CHAT = new Chat(1L, "type");
    private UnbindAskPasswordAction action;
    private Update update;

    @BeforeEach
    void setUp() {
        action = new UnbindAskPasswordAction();
        update = new Update();
        Message message = new Message();
        message.setChat(CHAT);
        update.setMessage(message);
    }

    @Test
    void whenHandleThenReturnAskPasswordMessage() {
        BotApiMethod<?> botApiMethod = action.handle(update).get();
        SendMessage sendMessage = (SendMessage) botApiMethod;
        assertThat(sendMessage.getText()).isEqualTo("Введите пароль:");
    }
}