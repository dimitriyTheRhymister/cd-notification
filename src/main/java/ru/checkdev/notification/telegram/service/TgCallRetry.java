package ru.checkdev.notification.telegram.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service; // <--- УДАЛИТЕ ЭТУ АННОТАЦИЮ
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.Profile;

/**
 * Обертка над TgCall, которая добавляет шаблон Retry для всех вызовов.
 */
public class TgCallRetry implements TgCall {

    private final TgCall delegate;
    private final Retry retry;

    public TgCallRetry(TgCall delegate, Retry retry) {
        this.delegate = delegate;
        this.retry = retry;
    }

    @Override
    public Mono<Profile> doGet(String url) {
        Profile result = retry.exec(() -> delegate.doGet(url).block(), null);
        return result == null ? Mono.empty() : Mono.just(result);
    }

    @Override
    public Mono<Object> doPost(String url, Profile profile) {
        Object result = retry.exec(() -> delegate.doPost(url, profile).block(), null);
        return result == null ? Mono.empty() : Mono.just(result);
    }

    @Override
    public Mono<Object> doPost(String url) {
        Object result = retry.exec(() -> delegate.doPost(url).block(), null);
        return result == null ? Mono.empty() : Mono.just(result);
    }
}