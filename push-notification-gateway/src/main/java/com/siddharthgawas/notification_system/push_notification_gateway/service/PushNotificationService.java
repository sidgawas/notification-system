package com.siddharthgawas.notification_system.push_notification_gateway.service;

import com.siddharthgawas.notification_system.push_notification_gateway.dto.SubscriptionRequest;
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

    /**
     * Subscribe topic.
     *
     * @param subscriptionId the subscription id
     * @param request        the request
     */
    void subscribeTopic(final String subscriptionId, final SubscriptionRequest request);

    /**
     * Unsubscribe topic.
     *
     * @param subscriptionId the subscription id
     * @param request        the request
     */
    void unsubscribeTopic(final String subscriptionId, final SubscriptionRequest request);
}
