package ru.checkdev.notification.telegram.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.Profile;
import ru.checkdev.notification.exception.AppException;
import ru.checkdev.notification.exception.IdNotFoundException;

/**
 * Класс реализует методы get и post для отправки сообщений через WebClient
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@org.springframework.context.annotation.Profile("default")
@Service
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class TgAuthCallWebClient implements TgCall {
    @Value("${server.auth}")
    private String urlServiceAuth;

    /**
     * Метод get
     *
     * @param url URL http
     * @return Mono<Person>
     */
    @Override
    public Mono<Profile> doGet(String url) {
        return WebClient.create(urlServiceAuth)
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Profile.class)
                .onErrorMap(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().value() == 404) {
                        return new IdNotFoundException("Профиль не найден: " + url);
                    }
                    return new AppException("Ошибка при обращении к сервису: " + ex.getStatusCode());
                })
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

    /**
     * Метод POST
     *
     * @param url     URL http
     * @param profile Body PersonDTO.class
     * @return Mono<Person>
     */
    @Override
    public Mono<Object> doPost(String url, Profile profile) {
        return WebClient.create(urlServiceAuth)
                .post()
                .uri(url)
                .bodyValue(profile)
                .retrieve()
                .bodyToMono(Object.class)
                .onErrorMap(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().value() == 404) {
                        return new IdNotFoundException("Ресурс не найден: " + url);
                    }
                    return new AppException("Ошибка при обращении к сервису: " + ex.getStatusCode());
                })
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

    @Override
    public Mono<Object> doPost(String url) {
        return WebClient.create(urlServiceAuth)
                .post()
                .uri(url)
                .retrieve()
                .bodyToMono(Object.class)
                .onErrorMap(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().value() == 404) {
                        return new IdNotFoundException("Ресурс не найден: " + url);
                    }
                    return new AppException("Ошибка при обращении к сервису: " + ex.getStatusCode());
                })
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }
}