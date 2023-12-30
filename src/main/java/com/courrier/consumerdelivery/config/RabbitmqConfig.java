package com.courrier.consumerdelivery.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitmqConfig {


    @Bean
    DirectExchange exchange() {
        return new DirectExchange("courrierchallenge");
    }

    @Bean
    public Queue createdDeliveryQueue() {
        return new Queue("createdDelivery", true);
    }

    @Bean
    Queue bonusModifiedQueue() {
        return new Queue("bonusModified", true);
    }

    @Bean
    Queue adjustmentModifiedQueue() {
        return new Queue("adjustmentModified", true);
    }

    @Bean
    Binding bindingCreatedDelivery(Queue createdDeliveryQueue, DirectExchange exchange) {
        return BindingBuilder.bind(createdDeliveryQueue).to(exchange).with("createdDelivery");
    }

}

