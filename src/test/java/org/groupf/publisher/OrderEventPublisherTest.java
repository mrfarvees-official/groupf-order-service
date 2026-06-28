package org.groupf.publisher;

import org.groupf.dto.OrderCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderEventPublisher orderEventPublisher;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(
                orderEventPublisher,
                "orderExchange",
                "order.exchange"
        );

        ReflectionTestUtils.setField(
                orderEventPublisher,
                "orderCreatedRoutingKey",
                "order.created.key"
        );
    }

    @Test
    void shouldPublishOrderCreatedEvent() {

        OrderCreatedEvent event = new OrderCreatedEvent(
                "order_1",
                "customer_1",
                "product_1",
                "Laptop",
                2,
                LocalDateTime.now(),
                "PENDING"
        );

        orderEventPublisher.publishOrderCreatedEvent(event);

        verify(rabbitTemplate, times(1))
                .convertAndSend(
                        "order.exchange",
                        "order.created.key",
                        event
                );
    }
}