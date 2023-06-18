package com.siddharthgawas.notification_system.push_notification_gateway.service.impl;

import com.siddharthgawas.notification_system.push_notification_gateway.service.PushNotificationEmitter;
import com.siddharthgawas.notification_system.push_notification_gateway.util.LoggingUtility;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import reactor.core.publisher.FluxSink;

import java.util.Map;
import java.util.Objects;

/**
 * The type Default push notification emitter.
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultPushNotificationEmitter implements PushNotificationEmitter<String> {

    /**
     * The Subscriber id.
     */
    @Getter
    private final String subscriberId;

    /**
     * The Sink.
     */
    private FluxSink<String> sink;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultPushNotificationEmitter that = (DefaultPushNotificationEmitter) o;
        return Objects.equals(getSubscriberId(), that.getSubscriberId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubscriberId());
    }

    @Override
    public void emit(final String event) {
        sink.next(event);
        LoggingUtility.logEvent(log, Level.INFO,
                "NOTIFICATION_PUSHED",
                Map.of("notificationBody", event, "subscriptionId", getSubscriberId()),
                "Notification was pushed" );
    }

    @Override
    public String getId() {
        return subscriberId;
    }

    @Override
    public void accept(FluxSink<String> stringFluxSink) {
        log.info("Subscription received for subscriber ID: {}", subscriberId);
        this.sink = stringFluxSink;
    }
}
