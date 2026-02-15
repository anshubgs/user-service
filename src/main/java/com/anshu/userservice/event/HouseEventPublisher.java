package com.anshu.userservice.event;

public interface HouseEventPublisher {

	void publish(HouseCreatedEvent event);
}
