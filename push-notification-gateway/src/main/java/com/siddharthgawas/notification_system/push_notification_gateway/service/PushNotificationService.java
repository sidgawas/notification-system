package com.siddharthgawas.notification_system.push_notification_gateway.service;

import reactor.core.publisher.Flux;

/**
 * The interface Push notification service.
 */
public interface PushNotificationService {

    /**
     * Gets notification stream.
     *
     * @param subscriptionId the subscription id
     * @return the notification stream
     */
    Flux<String> getNotificationStream(final String subscriptionId);
}
