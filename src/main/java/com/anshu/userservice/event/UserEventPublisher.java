package com.anshu.userservice.event;

public interface UserEventPublisher {
    void publish(UserSyncedEvent event);
}
