package com.anshu.userservice.listner;

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRegisteredPubSubListener {

    private final DeviceRegisteredEventListener deviceRegisteredEventListener;

    @ServiceActivator(inputChannel = "pubsubInputChannel")
    public void listen(
            String payload,
            @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE)
            BasicAcknowledgeablePubsubMessage message
    ) {

        try {

            log.info("######################################################");
            log.info("📥 RAW PUBSUB PAYLOAD");
            log.info("{}", payload);
            log.info("######################################################");

            deviceRegisteredEventListener.handleDeviceRegisteredEvent(payload);

            message.ack();

            log.info("✅ Message ACKED Successfully");

        } catch (Exception e) {

            log.error("❌ Processing Failed", e);

            message.nack();

            log.info("🔁 Message NACKED");
        }
    }
}