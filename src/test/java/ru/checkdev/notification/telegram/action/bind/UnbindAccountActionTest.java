package ru.checkdev.notification.telegram.action.bind;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.repository.SubscribeTopicRepositoryFake;
import ru.checkdev.notification.repository.UserTelegramRepositoryFake;
import ru.checkdev.notification.service.EurekaUriProvider;
import ru.checkdev.notification.service.UserTelegramService;
import ru.checkdev.notification.telegram.SessionTg;
import ru.checkdev.notification.telegram.service.FakeTgCallConsole;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnbindAccountActionTest {
    private static final Chat CHAT = new Chat(1L, "type");

    @Mock
    private DiscoveryClient discoveryClient;
    @Mock
    private ServiceInstance serviceInstance;
    private EurekaUriProvider uriProvider;
    private SessionTg sessionTg;
    private UserTelegramService userTelegramService;
    private UnbindAccountAction unbindAccountAction;
    private Update update;
    private Message message;

    @BeforeEach
    void setUp() {
        uriProvider = new EurekaUriProvider(discoveryClient);
        sessionTg = new SessionTg();
        userTelegramService = new UserTelegramService(
                new UserTelegramRepositoryFake(
                        new SubscribeTopicRepositoryFake()));
        unbindAccountAction = new UnbindAccountAction(
                sessionTg, new FakeTgCallConsole(uriProvider), userTelegramService);
        update = new Update();
        message = new Message();
    }

    @Test
    void whenUnbindWithBoundAccountThenOk() throws URISyntaxException {
        message.setChat(CHAT);
        update.setMessage(message);
        List<ServiceInstance> serviceInstances = Collections.singletonList(serviceInstance);
        Mockito.when(discoveryClient.getInstances(Mockito.anyString())).thenReturn(serviceInstances);
        Mockito.when(serviceInstance.getUri()).thenReturn(new URI("null"));
        UserTelegram userTelegram = new UserTelegram(0, -23, 1L, false);
        userTelegramService.save(userTelegram);
        sessionTg.put("1", "email", "fake@mail.ru");
        sessionTg.put("1", "password", "password");

        BotApiMethod<?> botApiMethod = unbindAccountAction.handle(update).get();
        SendMessage sendMessage = (SendMessage) botApiMethod;
        String actualMessage = sendMessage.getText();

        assertThat(actualMessage).contains("успешно отвязан");
    }

    @Test
    void whenUnbindWithoutBoundAccountThenMessageAccountIsNotBind() {
        message.setChat(CHAT);
        update.setMessage(message);
        String expectMessage = "К данному аккаунту Telegram не привязан аккаунт CheckDev";

        BotApiMethod<?> botApiMethod = unbindAccountAction.handle(update).get();
        SendMessage sendMessage = (SendMessage) botApiMethod;
        String actualMessage = sendMessage.getText();

        assertThat(actualMessage).isEqualTo(expectMessage);
    }
}