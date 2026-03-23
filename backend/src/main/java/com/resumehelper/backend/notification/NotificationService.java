package com.resumehelper.backend.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class NotificationService {

    @Value("${discord.webhook.url:}")
    private String discordWebhookUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void sendDiscordNotification(String message) {
        if (discordWebhookUrl == null || discordWebhookUrl.isBlank()) {
            return;
        }

        try {
            String jsonPayload = "{\"content\": \"" + message.replace("\"", "\\\"") + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(discordWebhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() >= 400) {
                            System.err.println("Failed to send Discord notification. Status: " + response.statusCode());
                        }
                    });

        } catch (Exception e) {
            System.err.println("Error sending Discord notification: " + e.getMessage());
        }
    }
}
