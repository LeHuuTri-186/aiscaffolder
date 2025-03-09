package com.aiscaffolder.aiscaffolder.application.services;

import com.aiscaffolder.aiscaffolder.config.SlackNotificationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SlackNotificationService {
    private final SlackNotificationConfig config;
    private final RestTemplate restTemplate = new RestTemplate();


    public SlackNotificationService(SlackNotificationConfig config) {
        this.config = config;
    }

    public void sendNotification(String message) {
        String url = config.getWebhookUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = Map.of("text", message);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(url, request, String.class);
    }
}
