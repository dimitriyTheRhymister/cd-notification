package ru.checkdev.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EurekaUriProvider {

    private final DiscoveryClient discoveryClient;

    @Value("${site.url:http://localhost:8080}")
    private String fallbackUrl;

    public String getUri(String serviceId) {
        List<ServiceInstance> list = discoveryClient.getInstances(serviceId);
        if (list.isEmpty()) {
            return fallbackUrl;
        }
        ServiceInstance instance = list.get(0);
        return instance.getUri().toString();
    }
}