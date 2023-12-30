package com.courrier.consumerdelivery.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.Serial;

@Service
public class ServiceMessageConsumer {
    @Component
    public class MessageConsumerService {
        @Autowired
        private RabbitTemplate rabbitTemplate;

        @RabbitListener(queues = "createdDelivery")
        public void consumeMessageCreated(String message) {
            System.out.println(1);
            System.out.println("Mensagem recebida: " + message);
        }

        @RabbitListener(queues = "adjustmentModified")
        public void consumeMessageAdjustment(String message) {

            System.out.println("Mensagem recebida: " + message);

        }

        @RabbitListener(queues = "bonusModified")
        public void consumeMessagBonus(String message) {

            System.out.println("Mensagem recebida: " + message);

        }
    }
}
