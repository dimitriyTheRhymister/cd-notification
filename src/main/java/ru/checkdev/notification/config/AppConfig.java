package ru.checkdev.notification.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import ru.checkdev.notification.handler.RestTemplateResponseErrorHandler;
import ru.checkdev.notification.telegram.service.FakeTgCallConsole;
import ru.checkdev.notification.telegram.service.Retry;
import ru.checkdev.notification.telegram.service.TgCallRetry;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClient;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

    @Bean
    public Retry retry() {
        return new Retry(3, 1000);
    }

    // Создаем TgCallRetry для профиля default, используя TgAuthCallWebClient
    @Bean
    @Profile("default")
    public TgCallRetry tgCallRetryDefault(TgAuthCallWebClient tgAuthCallWebClient, Retry retry) {
        return new TgCallRetry(tgAuthCallWebClient, retry);
    }

    // Создаем TgCallRetry для профиля develop, используя FakeTgCallConsole
    @Bean
    @Profile("develop")
    public TgCallRetry tgCallRetryDevelop(FakeTgCallConsole fakeTgCallConsole, Retry retry) {
        return new TgCallRetry(fakeTgCallConsole, retry);
    }
}