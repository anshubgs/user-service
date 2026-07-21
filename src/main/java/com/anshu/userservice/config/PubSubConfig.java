package com.anshu.userservice.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class PubSubConfig {

    @Value("${pubsub.subscription.device-registered}")
    private String deviceRegisteredSubscription;

    /**
     * Input channel for Device Registered events
     */
    @Bean
    public MessageChannel pubsubInputChannel() {
        return new DirectChannel();
    }

    /**
     * Pub/Sub Subscriber Adapter
     */
    @Bean
    public PubSubInboundChannelAdapter deviceRegisteredInboundAdapter(
            PubSubTemplate pubSubTemplate,
            MessageChannel pubsubInputChannel
    ) {

        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(
                        pubSubTemplate,
                        deviceRegisteredSubscription);

        adapter.setOutputChannel(pubsubInputChannel);

        // Since listener manually ACK/NACKs the message
        adapter.setAckMode(AckMode.MANUAL);

        return adapter;
    }
}