package com.anshu.userservice.listner;


import com.anshu.userservice.devicecache.repository.CachedDeviceRepository;
import com.anshu.userservice.event.DeviceRegisteredEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRegisteredPubSubListener {

    private final ObjectMapper objectMapper;
    private final DeviceRegisteredEventListener deviceRegisteredEventListener; // use your existing class
    private final CachedDeviceRepository cachedDeviceRepository;

    @Value("${pubsub.subscription.device-registered}")
    private String subscription;

    /**
     * This method is triggered automatically when a message arrives on the Pub/Sub subscription
     */
    @ServiceActivator(inputChannel = "pubsubInputChannel")
    public void listen(
            String payload,
            @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message
    ) {
        try {
            log.info("📥 Received message from Pub/Sub | payload={}", payload);

            // Call your existing handler
            deviceRegisteredEventListener.handleDeviceRegisteredEvent(payload);

            // ✅ ACK the message after successful processing
            message.ack();
            log.info("✅ DeviceRegisteredEvent processed & acknowledged");

        } catch (Exception e) {
            log.error("❌ Error processing DeviceRegisteredEvent", e);
            // NACK so Pub/Sub can retry / DLQ
            message.nack();
        }
    }
}