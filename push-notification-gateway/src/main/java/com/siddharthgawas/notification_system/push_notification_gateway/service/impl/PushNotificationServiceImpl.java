package com.siddharthgawas.notification_system.push_notification_gateway.service.impl;

import com.siddharthgawas.notification_system.push_notification_gateway.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The type Push notification service.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class PushNotificationServiceImpl implements PushNotificationService {

    /**
     * The Amqp admin.
     */
    private final AmqpAdmin amqpAdmin;

    /**
     * The Connection factory.
     */
    private final ConnectionFactory connectionFactory;
    /**
     * The Containers.
     */
    private Map<String, AbstractMessageListenerContainer> containers = new HashMap<>();

    @Override
    public Flux<String> getNotificationStream(final String subscriptionId) {
        declareAndBindQueue(subscriptionId);

        final var eventEmitter = new DefaultPushNotificationEmitter(subscriptionId);
        final var listener = new MessageListenerAdapter();
        listener.setDelegate(eventEmitter);
        listener.setDefaultListenerMethod("emit");
        listener.setMessageConverter(new SimpleMessageConverter());

        final var container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(getQueueName(subscriptionId));
        container.setMessageListener(listener);

        final var containerKey = subscriptionId + "-" + UUID.randomUUID();
        containers.put(containerKey, container);
        container.start();
        return Flux.create(eventEmitter).doOnCancel(() -> {
                    log.info("Connection {} is being closed.", containerKey);
                    containers.get(containerKey).stop();
                    containers.remove(containerKey);
                });
    }

    /**
     * Declare and bind queue.
     *
     * @param subscriptionId the subscription id
     */
    private void declareAndBindQueue(final String subscriptionId) {
        final var queue = new Queue(getQueueName(subscriptionId));
        final var exchange = ExchangeBuilder.fanoutExchange("notify.all")
                .durable(true).build();
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with("").noargs());
    }

    /**
     * Gets queue name.
     *
     * @param subscriptionId the subscription id
     * @return the queue name
     */
    private String getQueueName(final String subscriptionId) {
        return "subscription." + subscriptionId;
    }
}
