package ru.checkdev.notification.telegram.action.unbind;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.checkdev.notification.telegram.SessionTg;
import ru.checkdev.notification.telegram.action.Action;

import java.util.Optional;

/**
 * /unbind — шаг 4: сохраняем пароль в сессию.
 */
@AllArgsConstructor
public class UnbindPutPasswordAction implements Action {
    private final SessionTg sessionTg;

    @Override
    public Optional<BotApiMethod> handle(Update update) {
        var msg = update.getMessage();
        var chatId = msg.getChatId().toString();
        var password = msg.getText();
        sessionTg.put(chatId, "password", password);
        return Optional.empty();
    }
}