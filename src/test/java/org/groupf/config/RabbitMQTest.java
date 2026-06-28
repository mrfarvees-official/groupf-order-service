package org.groupf.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RabbitMQConfig.class, RabbitMQConfigTest.TestConfig.class})
@TestPropertySource(properties = {
        "rabbitmq.exchange.order=order-exchange",
        "rabbitmq.queue.notification-order=notification-order-queue",
        "rabbitmq.routing-key.order-created=order-created-routing-key"
})
class RabbitMQConfigTest {

    @Configuration
    static class TestConfig {
        @Bean
        public ConnectionFactory connectionFactory() {
            return mock(ConnectionFactory.class);
        }
    }

    @Autowired
    private TopicExchange orderExchange;

    @Autowired
    private Queue notificationOrderQueue;

    @Autowired
    private Binding orderCreatedBinding;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void shouldLoadRabbitMQBeans() {

        assertNotNull(orderExchange);
        assertEquals(TopicExchange.class, orderExchange.getClass());

        assertNotNull(notificationOrderQueue);
        assertTrue(notificationOrderQueue.isDurable());

        assertNotNull(orderCreatedBinding);

        assertNotNull(rabbitTemplate);
    }

    @Test
    void shouldBindQueueToExchangeCorrectly() {
        assertEquals(
                orderExchange.getName(),
                orderCreatedBinding.getExchange()
        );

        assertEquals(
                notificationOrderQueue.getName(),
                orderCreatedBinding.getDestination()
        );
    }
}