package com.anshu.userservice.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PubSubHouseEventPublisher implements HouseEventPublisher {

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper;

    @Value("${house.pubsub.topic}")
    private String topicName;

    @Override
    public void publish(HouseCreatedEvent event) {

        try {
            String payload = objectMapper.writeValueAsString(event);

            log.info("Publishing HouseCreatedEvent to topic [{}]: {}", topicName, payload);

            CompletableFuture<String> future = pubSubTemplate.publish(topicName, payload);

            future.whenComplete((messageId, throwable) -> {
                if (throwable != null) {
                    log.error("Failed to publish HouseCreatedEvent", throwable);
                } else {
                    log.info("HouseCreatedEvent published successfully. MessageId={}", messageId);
                }
            });

        } catch (JsonProcessingException e) {
            log.error("Error serializing HouseCreatedEvent", e);
            throw new IllegalStateException("Failed to serialize HouseCreatedEvent", e);
        }
    }
}
