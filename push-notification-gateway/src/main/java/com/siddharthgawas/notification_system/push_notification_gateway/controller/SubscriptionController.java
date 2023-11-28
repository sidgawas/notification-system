package com.siddharthgawas.notification_system.push_notification_gateway.controller;

import com.siddharthgawas.notification_system.push_notification_gateway.dto.SubscriptionRequest;
import com.siddharthgawas.notification_system.push_notification_gateway.service.PushNotificationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    /**
     * Subscribe topic mono.
     *
     * @param subscriptionId the subscription id
     * @param request        the request
     * @return the mono
     */
    @PostMapping(value = "/push_notifications/{subscriptionID}/topic")
    public Mono<ResponseEntity<Object>> subscribeTopic(@PathVariable("subscriptionID") final String subscriptionId,
                                                 @RequestBody SubscriptionRequest request) {
        pushNotificationService.subscribeTopic(subscriptionId, request);
        return Mono.just(ResponseEntity.status(HttpStatus.CREATED).build());
    }


    /**
     * Unsubscribe topic mono.
     *
     * @param subscriptionId the subscription id
     * @param request        the request
     * @return the mono
     */
    @DeleteMapping(value = "/push_notifications/{subscriptionID}/topic")
    public Mono<ResponseEntity<Object>> unsubscribeTopic(@PathVariable("subscriptionID") final String subscriptionId,
                                                       @RequestBody SubscriptionRequest request) {
        pushNotificationService.unsubscribeTopic(subscriptionId, request);
        return Mono.just(ResponseEntity.status(HttpStatus.OK).build());
    }
}
