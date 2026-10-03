package ru.checkdev.notification.telegram.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.Profile;

/**
 * Класс реализует методы get и post для отправки сообщений через WebClient
 * с использованием Retry и Circuit Breaker из Resilience4j.
 */
@org.springframework.context.annotation.Profile("default")
@Service
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class TgAuthCallWebClient implements TgCall {

    @Value("${server.auth}")
    private String urlServiceAuth;

    @Retry(name = "tgAuthRetry")
    @CircuitBreaker(name = "tgAuthCircuitBreaker", fallbackMethod = "fallbackGet")
    @Override
    public Mono<Profile> doGet(String url) {
        return WebClient.create(urlServiceAuth)
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Profile.class)
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

    @Retry(name = "tgAuthRetry")
    @CircuitBreaker(name = "tgAuthCircuitBreaker", fallbackMethod = "fallbackPostProfile")
    @Override
    public Mono<Object> doPost(String url, Profile profile) {
        return WebClient.create(urlServiceAuth)
                .post()
                .uri(url)
                .bodyValue(profile)
                .retrieve()
                .bodyToMono(Object.class)
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

    @Retry(name = "tgAuthRetry")
    @CircuitBreaker(name = "tgAuthCircuitBreaker", fallbackMethod = "fallbackPost")
    @Override
    public Mono<Object> doPost(String url) {
        return WebClient.create(urlServiceAuth)
                .post()
                .uri(url)
                .retrieve()
                .bodyToMono(Object.class)
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

    // Fallback для doGet
    public Mono<Profile> fallbackGet(String url, Throwable throwable) {
        log.error("GET fallback triggered: {}", throwable.getMessage());
        return Mono.empty();
    }

    // Fallback для doPost(url, profile)
    public Mono<Object> fallbackPostProfile(String url, Profile profile, Throwable throwable) {
        log.error("POST (with profile) fallback triggered: {}", throwable.getMessage());
        return Mono.empty();
    }

    // Fallback для doPost(url)
    public Mono<Object> fallbackPost(String url, Throwable throwable) {
        log.error("POST fallback triggered: {}", throwable.getMessage());
        return Mono.empty();
    }
}