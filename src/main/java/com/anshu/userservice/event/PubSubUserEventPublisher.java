package com.anshu.userservice.event;

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
public class PubSubUserEventPublisher implements UserEventPublisher {

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper objectMapper;

    @Value("${user.pubsub.topic}")
    private String topicName;

    @Override
    public void publish(UserSyncedEvent event) {

        try {

            String payload = objectMapper.writeValueAsString(event);

            log.info("[USER EVENT] Publishing to topic [{}] → userUuid={}, role={}, houseUuid={}",
                    topicName,
                    event.userUuid(),
                    event.role(),
                    event.houseUuid()
            );

            CompletableFuture<String> future =
                    pubSubTemplate.publish(topicName, payload);

            future.whenComplete((messageId, throwable) -> {
                if (throwable != null) {
                    log.error("[USER EVENT FAILED] userUuid={}", event.userUuid(), throwable);
                } else {
                    log.info("[USER EVENT SUCCESS] MessageId={} userUuid={}",
                            messageId,
                            event.userUuid());
                }
            });

        } catch (Exception e) {
            log.error("[USER EVENT SERIALIZATION ERROR]", e);
            throw new IllegalStateException("User event publish failed", e);
        }
    }
}
