package com.siddharthgawas.notification_system.push_notification_gateway.service.impl;

import com.siddharthgawas.notification_system.push_notification_gateway.dto.SubscriptionRequest;
import com.siddharthgawas.notification_system.push_notification_gateway.service.PushNotificationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
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

    /**
     * The Notify all.
     */
    private Exchange notifyAll;

    /**
     * The Notify topic subscribers.
     */
    private Exchange notifyTopicSubscribers;

    /**
     * Post construct.
     */
    @PostConstruct
    public void postConstruct() {
        notifyAll = ExchangeBuilder
                .fanoutExchange("notify.all").durable(true).build();
        amqpAdmin.declareExchange(notifyAll);
        notifyTopicSubscribers = ExchangeBuilder
                .directExchange("notify.topic").durable(true).build();
        amqpAdmin.declareExchange(notifyTopicSubscribers);
    }

    /**
     * Gets notification stream.
     *
     * @param subscriptionId the subscription id
     * @return the notification stream
     */
    @Override
    public Flux<String> getNotificationStream(final String subscriptionId) {
        declareAndBindQueue(subscriptionId, notifyAll, "");
        final var eventEmitter = new DefaultPushNotificationEmitter(subscriptionId);
        final var listener = new MessageListenerAdapter();
        listener.setDelegate(eventEmitter);
        listener.setDefaultListenerMethod("emit");
        listener.setMessageConverter(new SimpleMessageConverter());
        final var container = new SimpleMessageListenerContainer();
        final var containerKey = subscriptionId + "-" + UUID.randomUUID();

        return Flux.create(eventEmitter)
                .doOnSubscribe(subscription -> {
                    container.setConnectionFactory(connectionFactory);
                    container.setMessageListener(listener);
                    containers.put(containerKey, container);
                    container.setQueueNames(getQueueName(subscriptionId));
                    container.start();
                })
                .doOnCancel(() -> {
                    log.info("Connection {} is being closed.", containerKey);
                    containers.get(containerKey).stop();
                    containers.remove(containerKey);
                });
    }

    /**
     * Subscribe topic.
     *
     * @param subscriptionId the subscription id
     * @param request        the request
     */
    @Override
    public void subscribeTopic(final String subscriptionId, final SubscriptionRequest request) {
        declareAndBindQueue(subscriptionId, notifyTopicSubscribers, request.topicName());
        declareAndBindQueue(subscriptionId, notifyAll, "");
    }

    /**
     * Unsubscribe topic.
     *
     * @param subscriptionId the subscription id
     * @param request        the request
     */
    @Override
    public void unsubscribeTopic(final String subscriptionId, final SubscriptionRequest request) {
        unbindQueue(subscriptionId, notifyTopicSubscribers, request.topicName());
    }

    /**
     * Declare and bind queue.
     *
     * @param subscriptionId the subscription id
     * @param exchange       the exchange
     * @param routingKey     the routing key
     */
    private void declareAndBindQueue(final String subscriptionId, final Exchange exchange, final String routingKey) {
        final var queue = new Queue(getQueueName(subscriptionId));
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs());
    }


    /**
     * Unbind queue.
     *
     * @param subscriptionId the subscription id
     * @param exchange       the exchange
     * @param routingKey     the routing key
     */
    private void unbindQueue(final String subscriptionId, final Exchange exchange, final String routingKey) {
        final var queue = new Queue(getQueueName(subscriptionId));
        amqpAdmin.removeBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs());
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
