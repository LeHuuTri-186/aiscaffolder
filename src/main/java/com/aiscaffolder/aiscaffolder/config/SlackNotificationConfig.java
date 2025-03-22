package com.aiscaffolder.aiscaffolder.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class SlackNotificationConfig {
    @Value("${slack.webhook-url}")
    private String webhookUrl;
}
