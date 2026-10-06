package ru.checkdev.notification.telegram.action.bind;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.checkdev.notification.domain.Profile;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.dto.ProfileTgDTO;
import ru.checkdev.notification.service.UserTelegramService;
import ru.checkdev.notification.telegram.SessionTg;
import ru.checkdev.notification.telegram.action.Action;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgCall;

import java.util.Optional;

/**
 * /unbind — шаг 5: проверяем email и пароль, отвязываем аккаунт.
 */
@AllArgsConstructor
@Slf4j
public class UnbindAccountAction implements Action {
    private static final String URL_PROFILE_BY_EMAIL_AND_PASS = "/profiles/tg/byEmailAndPassword";
    private final TgConfig tgConfig = new TgConfig(0);
    private final SessionTg sessionTg;
    private final TgCall tgCall;
    private final UserTelegramService userTelegramService;

    @Override
    public Optional<BotApiMethod> handle(Update update) {
        Object result;
        var text = "";
        var ls = System.lineSeparator();
        var chatId = update.getMessage().getChatId();
        var email = sessionTg.get(chatId.toString(), "email", "");
        var password = sessionTg.get(chatId.toString(), "password", "");

        // 1. Проверяем, привязан ли аккаунт Telegram вообще
        Optional<UserTelegram> userTgOptional = userTelegramService.findByChatId(chatId);
        if (userTgOptional.isEmpty()) {
            text = "К данному аккаунту Telegram не привязан аккаунт CheckDev";
            return Optional.of(new SendMessage(chatId.toString(), text));
        }

        // 2. Проверяем email и пароль через сервис auth
        var profile = new Profile();
        profile.setEmail(email);
        profile.setPassword(password);
        try {
            result = tgCall.doPost(URL_PROFILE_BY_EMAIL_AND_PASS, profile).block();
            if (result == null) {
                text = "Пользователь не найден";
            } else {
                var profileTg = tgConfig.getMapper().convertValue(result, ProfileTgDTO.class);
                UserTelegram userTg = userTgOptional.get();

                // 3. Проверяем, что email совпадает с привязанным userId
                if (profileTg.getId() == userTg.getUserId()) {
                    userTelegramService.delete(userTg);
                    text = "Ваш аккаунт CheckDev успешно отвязан от данного аккаунта Telegram";
                } else {
                    text = "Данный аккаунт CheckDev привязан к другому аккаунту Telegram";
                }
            }
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = String.format("Сервис недоступен, попробуйте позже%s%s", ls, "/start");
        }
        return Optional.of(new SendMessage(chatId.toString(), text));
    }
}