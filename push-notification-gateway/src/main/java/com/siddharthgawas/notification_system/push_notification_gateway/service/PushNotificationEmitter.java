package com.siddharthgawas.notification_system.push_notification_gateway.service;

import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

/**
 * The interface Push notification emitter.
 *
 * @param <T> the type parameter
 */
public interface PushNotificationEmitter<T> extends Consumer<FluxSink<T>> {

    /**
     * Emit.
     *
     * @param event the event
     */
    void emit(final T event);

    /**
     * Gets id.
     *
     * @return the id
     */
    String getId();
}
