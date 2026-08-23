package ru.checkdev.notification.telegram;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import ru.checkdev.notification.telegram.service.Retry;

@Slf4j
public class RetryTest {

    @Test
    public void whenRetryExecutesThenRetriesFakeTgCallConsole() {
        // Создаем Retry с 3 попытками и задержкой 100 мс (для быстрого теста)
        Retry retry = new Retry(3, 100);

        // Имитируем действие, которое всегда выбрасывает исключение
        Retry.Act<String> act = () -> {
            throw new RuntimeException("Сервис auth недоступен");
        };

        // Выполняем с Retry и возвращаем значение по умолчанию
        String result = retry.exec(act, "Сервис временно недоступен");

        // Логируем результат (должно быть "Сервис временно недоступен" после 3 попыток)
        log.info("Результат после Retry: {}", result);
    }
}