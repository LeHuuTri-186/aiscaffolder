package com.aiscaffolder.projecttemplateengine.application.services;

import com.aiscaffolder.projecttemplateengine.config.SlackNotificationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class SlackNotificationService {
    private final SlackNotificationConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    public SlackNotificationService(SlackNotificationConfig config) {
        this.config = config;
    }

    public void sendNotification(String message) {
        log.info("{}", config.getWebhookUrl());

        String url = config.getWebhookUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = Map.of("text", message);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(url, request, String.class);
    }
}
