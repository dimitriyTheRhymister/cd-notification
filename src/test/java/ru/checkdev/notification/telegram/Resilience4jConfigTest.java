package ru.checkdev.notification.telegram;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тест, который проверяет работу Retry и Circuit Breaker от Resilience4j.
 * Тест не использует Spring, а работает с API Resilience4j напрямую.
 */
@Slf4j
class Resilience4jConfigTest {

    private static final String CB_NAME = "tgAuthCircuitBreaker";
    private static final String RETRY_NAME = "tgAuthRetry";

    private CircuitBreakerRegistry cbRegistry;
    private RetryRegistry retryRegistry;

    @BeforeEach
    void setUp() {
        // Настраиваем Circuit Breaker, как в application.properties
        CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)              // 50% ошибок — открыть
                .waitDurationInOpenState(Duration.ofMillis(1000)) // 1 сек в Open
                .slidingWindowSize(10)                 // окно из 10 запросов
                .minimumNumberOfCalls(3)               // минимум 3 запроса
                .permittedNumberOfCallsInHalfOpenState(2)
                .build();
        cbRegistry = CircuitBreakerRegistry.of(cbConfig);

        // Настраиваем Retry, как в application.properties
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)                        // 3 попытки
                .waitDuration(Duration.ofMillis(100))  // 100 мс между попытками
                .build();
        retryRegistry = RetryRegistry.of(retryConfig);
    }

    /**
     * Тест 1: Retry должен повторить действие 3 раза.
     */
    @Test
    void whenRetryExecutesThenItRetriesThreeTimes() {
        AtomicInteger counter = new AtomicInteger(0);
        Retry retry = retryRegistry.retry(RETRY_NAME);

        try {
            Retry.decorateCheckedRunnable(retry, () -> {
                counter.incrementAndGet();
                throw new RuntimeException("Тестовая ошибка");
            }).run();
        } catch (Throwable e) {
            log.info("Все попытки провалились: {}", e.getMessage());
        }

        assertThat(counter.get())
                .as("Retry должен был выполнить 3 попытки")
                .isEqualTo(3);
    }

    /**
     * Тест 2: Circuit Breaker должен открыться после 3 ошибок.
     */
    @Test
    void whenCircuitBreakerExceedsThresholdThenItOpens() {
        CircuitBreaker circuitBreaker = cbRegistry.circuitBreaker(CB_NAME);

        // Делаем 3 неудачных вызова (minimumNumberOfCalls = 3)
        for (int i = 0; i < 3; i++) {
            try {
                int finalI = i;
                circuitBreaker.executeCheckedRunnable(() -> {
                    throw new RuntimeException("Ошибка " + finalI);
                });
            } catch (Throwable e) {
                log.info("Попытка {} провалилась: {}", i + 1, e.getMessage());
            }
        }

        // Проверяем, что Circuit Breaker ОТКРЫТ
        assertThat(circuitBreaker.getState())
                .as("Circuit Breaker должен быть OPEN после 3 ошибок")
                .isEqualTo(CircuitBreaker.State.OPEN);

        // Проверяем, что следующий вызов блокируется
        boolean callBlocked = false;
        try {
            circuitBreaker.executeCheckedRunnable(() -> log.info("Этот код не должен выполниться"));
        } catch (Throwable e) {
            callBlocked = true;
            log.info("Вызов заблокирован: {}", e.getClass().getSimpleName());
        }
        assertThat(callBlocked)
                .as("Circuit Breaker должен блокировать вызовы в состоянии OPEN")
                .isTrue();
    }

    /**
     * Тест 3: Circuit Breaker + Retry вместе.
     * Retry делает 3 попытки, а после 3-х неудач Circuit Breaker открывается.
     */
    @Test
    void whenRetryAndCircuitBreakerCombinedThenCircuitOpensAfterRetries() {
        CircuitBreaker circuitBreaker = cbRegistry.circuitBreaker(CB_NAME);
        Retry retry = retryRegistry.retry(RETRY_NAME);

        // Оборачиваем действие в Circuit Breaker, потом в Retry
        Runnable decorated = Retry.decorateRunnable(retry,
                CircuitBreaker.decorateRunnable(circuitBreaker, () -> {
                    throw new RuntimeException("Комбинированная ошибка");
                }));

        // Выполняем 3 раза — Retry отработает 3 раза, потом Circuit Breaker откроется
        try {
            decorated.run();
        } catch (Throwable e) {
            log.info("Комбинированный вызов провалился: {}", e.getMessage());
        }

        log.info("Состояние Circuit Breaker: {}", circuitBreaker.getState());
        log.info("Количество неудачных вызовов: {}", circuitBreaker.getMetrics().getNumberOfFailedCalls());

        assertThat(circuitBreaker.getState())
                .as("После комбинированного вызова с ошибками Circuit Breaker должен открыться")
                .isEqualTo(CircuitBreaker.State.OPEN);
    }
}