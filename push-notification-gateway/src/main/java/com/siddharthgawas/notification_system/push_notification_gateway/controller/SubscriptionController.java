package com.siddharthgawas.notification_system.push_notification_gateway.controller;

import com.siddharthgawas.notification_system.push_notification_gateway.service.PushNotificationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * The type Subscription controller.
 */
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SubscriptionController {

    /**
     * The Push notification service.
     */
    private final PushNotificationService pushNotificationService;

    /**
     * Subscribe flux.
     *
     * @param subscriptionId the subscription id
     * @return the flux
     */
    @GetMapping(value = "/push_notifications/{subscriptionID}")
    public Flux<ServerSentEvent<Object>> subscribe(@PathVariable("subscriptionID") final String subscriptionId) {
        return pushNotificationService.getNotificationStream(subscriptionId)
                .map(data -> ServerSentEvent.builder()
                        .id(UUID.randomUUID().toString())
                        .comment("Notification Event")
                        .data(data).event("notification-event").build());
    }
}
