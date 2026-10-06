package ru.checkdev.notification.telegram.action.unbind;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.checkdev.notification.telegram.action.Action;

import java.util.Optional;

/**
 * /unbind — шаг 1: спрашиваем email.
 */
@AllArgsConstructor
public class UnbindAskEmailAction implements Action {

    @Override
    public Optional<BotApiMethod> handle(Update update) {
        var chatId = update.getMessage().getChatId();
        var text = "Введите email пользователя:";
        return Optional.of(new SendMessage(chatId.toString(), text));
    }
}