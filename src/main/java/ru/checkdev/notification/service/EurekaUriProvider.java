package ru.checkdev.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EurekaUriProvider {

    private final DiscoveryClient discoveryClient;

    public String getUri(String serviceId) {
        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);
        if (list.isEmpty()) {
            // Если сервис не найден в Eureka, возвращаем запасной URL
            // (чтобы сервис мог работать автономно в профиле develop)
            return "http://localhost:8080"; // Например, URL сайта по умолчанию
        }
        ServiceInstance instance = list.get(0);
        return instance.getUri().toString();
    }
}